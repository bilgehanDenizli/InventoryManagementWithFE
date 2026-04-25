import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { InventoryFilterType } from '../../models/filter-type.model';
import { Inventory } from '../../models/inventory.model';
import { Product } from '../../models/product.model';
import { Warehouse } from '../../models/warehouse.model';
import { InventoryService } from '../../services/inventory.service';
import { ProductService } from '../../services/product.service';
import { WarehouseRequest, WarehouseService } from '../../services/warehouse.service';
import { extractApiError } from '../../utils/http-error.util';

interface FilterOption {
  label: string;
  value: InventoryFilterType;
  placeholder: string;
}

@Component({
  selector: 'app-inventory-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './inventory-page.component.html',
  styleUrl: './inventory-page.component.scss'
})
export class InventoryPageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);

  readonly filterOptions: FilterOption[] = [
    { label: 'Warehouse Name', value: 'WAREHOUSE_NAME', placeholder: 'warehouse a' },
    { label: 'Warehouse City', value: 'WAREHOUSE_CITY', placeholder: 'antalya' },
    { label: 'Warehouse Region', value: 'WAREHOUSE_REGION', placeholder: 'akdeniz' },
    { label: 'Product Name', value: 'PRODUCT_NAME', placeholder: 'Roman 1' },
    { label: 'Product ID', value: 'PRODUCT_ID', placeholder: '1' },
    { label: 'Product Category', value: 'PRODUCT_CATEGORY', placeholder: 'Kitap' }
  ];

  readonly inventoryItems = signal<Inventory[]>([]);
  readonly products = signal<Product[]>([]);
  readonly warehouses = signal<Warehouse[]>([]);

  readonly isLoadingInventory = signal(false);
  readonly isAdjusting = signal(false);
  readonly isDeleting = signal(false);

  readonly message = signal('');
  readonly error = signal('');

  private hasSeededSearch = false;

  readonly inventoryFilterForm = this.formBuilder.nonNullable.group({
    filterType: ['WAREHOUSE_NAME' as InventoryFilterType, Validators.required],
    filterValue: ['', [Validators.required, Validators.maxLength(255)]]
  });

  readonly stockAdjustmentForm = this.formBuilder.nonNullable.group({
    productId: [0, [Validators.required, Validators.min(1)]],
    warehouseId: [0, [Validators.required, Validators.min(1)]],
    amount: [1, [Validators.required, Validators.min(1)]],
    action: ['ADD' as 'ADD' | 'TAKE', Validators.required]
  });

  readonly removalForm = this.formBuilder.nonNullable.group({
    productId: [0, [Validators.required, Validators.min(1)]],
    warehouseId: [0, [Validators.required, Validators.min(1)]]
  });

  readonly totalUnits = computed(() =>
    this.inventoryItems().reduce((sum, item) => sum + item.amount, 0)
  );

  readonly activeFilterPlaceholder = computed(() => {
    const selected = this.filterOptions.find(
      (option) => option.value === this.inventoryFilterForm.controls.filterType.value
    );

    return selected?.placeholder ?? '';
  });

  constructor(
    private readonly inventoryService: InventoryService,
    private readonly productService: ProductService,
    private readonly warehouseService: WarehouseService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadWarehouses();
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products.set(products);

        if (this.stockAdjustmentForm.controls.productId.value === 0 && products.length > 0) {
          this.stockAdjustmentForm.patchValue({ productId: products[0].id });
          this.removalForm.patchValue({ productId: products[0].id });
        }
      },
      error: (error) => this.setError(extractApiError(error, 'Failed to load products for inventory actions.'))
    });
  }

  loadWarehouses(): void {
    const request: WarehouseRequest = {
      page: 0,
      limit: 25
    };

    this.warehouseService.getWarehouses(request).subscribe({
      next: (warehouses) => {
        this.warehouses.set(warehouses);

        if (this.stockAdjustmentForm.controls.warehouseId.value === 0 && warehouses.length > 0) {
          this.stockAdjustmentForm.patchValue({ warehouseId: warehouses[0].id });
          this.removalForm.patchValue({ warehouseId: warehouses[0].id });
        }

        if (!this.hasSeededSearch && warehouses.length > 0) {
          this.inventoryFilterForm.patchValue({ filterValue: warehouses[0].name });
          this.searchInventory();
          this.hasSeededSearch = true;
        }
      },
      error: (error) => this.setError(extractApiError(error, 'Failed to load warehouses for inventory actions.'))
    });
  }

  searchInventory(): void {
    if (this.inventoryFilterForm.invalid) {
      this.inventoryFilterForm.markAllAsTouched();
      return;
    }

    this.isLoadingInventory.set(true);
    this.clearFeedback();

    this.inventoryService
      .getInventory({
        filterType: this.inventoryFilterForm.controls.filterType.value,
        filterValue: this.inventoryFilterForm.controls.filterValue.value.trim()
      })
      .subscribe({
        next: (inventory) => {
          this.inventoryItems.set(inventory);
          this.isLoadingInventory.set(false);
        },
        error: (error) => {
          this.inventoryItems.set([]);
          this.isLoadingInventory.set(false);
          this.setError(extractApiError(error, 'Failed to load inventory.'));
        }
      });
  }

  submitStockAdjustment(): void {
    if (this.stockAdjustmentForm.invalid) {
      this.stockAdjustmentForm.markAllAsTouched();
      return;
    }

    this.isAdjusting.set(true);
    this.clearFeedback();

    const rawAmount = this.stockAdjustmentForm.controls.amount.value;
    const amount =
      this.stockAdjustmentForm.controls.action.value === 'TAKE' ? -rawAmount : rawAmount;

    this.inventoryService
      .adjustStock({
        productId: this.stockAdjustmentForm.controls.productId.value,
        warehouseId: this.stockAdjustmentForm.controls.warehouseId.value,
        amount
      })
      .subscribe({
        next: () => {
          this.isAdjusting.set(false);
          this.setMessage('Inventory updated.');
          this.searchInventory();
        },
        error: (error) => {
          this.isAdjusting.set(false);
          this.setError(extractApiError(error, 'Failed to update inventory.'));
        }
      });
  }

  submitRemoval(): void {
    if (this.removalForm.invalid) {
      this.removalForm.markAllAsTouched();
      return;
    }

    this.isDeleting.set(true);
    this.clearFeedback();

    this.inventoryService
      .deleteInventoryItem({
        productId: this.removalForm.controls.productId.value,
        warehouseId: this.removalForm.controls.warehouseId.value
      })
      .subscribe({
        next: () => {
          this.isDeleting.set(false);
          this.setMessage('Inventory entry removed.');
          this.searchInventory();
        },
        error: (error) => {
          this.isDeleting.set(false);
          this.setError(extractApiError(error, 'Failed to delete inventory entry.'));
        }
      });
  }

  private clearFeedback(): void {
    this.message.set('');
    this.error.set('');
  }

  private setMessage(message: string): void {
    this.message.set(message);
    this.error.set('');
  }

  private setError(message: string): void {
    this.error.set(message);
    this.message.set('');
  }
}
