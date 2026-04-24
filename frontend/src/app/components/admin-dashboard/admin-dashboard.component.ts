import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Product } from '../../models/product.model';
import { ProductCategory } from '../../models/product-category.model';
import { Warehouse } from '../../models/warehouse.model';
import { Inventory } from '../../models/inventory.model';
import { InventoryHistory } from '../../models/inventory-history.model';
import { InventoryFilterType } from '../../models/filter-type.model';
import { WarehouseRequest, WarehouseService } from '../../services/warehouse.service';
import { ProductCategoryService } from '../../services/product-category.service';
import { ProductService } from '../../services/product.service';
import { InventoryHistoryService } from '../../services/inventory-history.service';
import { InventoryService } from '../../services/inventory.service';

interface FilterOption {
  label: string;
  value: InventoryFilterType;
  placeholder: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);

  readonly warehouses = signal<Warehouse[]>([]);
  readonly products = signal<Product[]>([]);
  readonly categories = signal<ProductCategory[]>([]);
  readonly inventoryItems = signal<Inventory[]>([]);
  readonly historyItems = signal<InventoryHistory[]>([]);

  readonly isBootstrapping = signal(true);
  readonly isSavingProduct = signal(false);
  readonly isAdjustingInventory = signal(false);
  readonly isLoadingInventory = signal(false);
  readonly isLoadingHistory = signal(false);

  readonly currentWarehousePage = signal(0);
  readonly currentHistoryPage = signal(0);
  readonly warehousePageSize = 10;
  readonly historyPageSize = 10;

  readonly selectedProductId = signal<number | null>(null);

  readonly globalMessage = signal('');
  readonly globalError = signal('');

  readonly filterOptions: FilterOption[] = [
    { label: 'Warehouse Name', value: 'WAREHOUSE_NAME', placeholder: 'e.g. warehouse a' },
    { label: 'Warehouse City', value: 'WAREHOUSE_CITY', placeholder: 'e.g. antalya' },
    { label: 'Warehouse Region', value: 'WAREHOUSE_REGION', placeholder: 'e.g. akdeniz' },
    { label: 'Product Name', value: 'PRODUCT_NAME', placeholder: 'e.g. Roman 1' },
    { label: 'Product ID', value: 'PRODUCT_ID', placeholder: 'e.g. 1' },
    { label: 'Product Category', value: 'PRODUCT_CATEGORY', placeholder: 'e.g. Kitap' }
  ];

  readonly productForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    categoryId: [0, [Validators.min(1)]]
  });

  readonly inventoryFilterForm = this.formBuilder.nonNullable.group({
    filterType: ['WAREHOUSE_NAME' as InventoryFilterType, Validators.required],
    filterValue: ['', [Validators.required, Validators.maxLength(255)]]
  });

  readonly stockAdjustmentForm = this.formBuilder.nonNullable.group({
    productId: [0, [Validators.min(1)]],
    warehouseId: [0, [Validators.min(1)]],
    amount: [1, [Validators.required, Validators.min(1)]],
    action: ['ADD' as 'ADD' | 'TAKE', Validators.required]
  });

  readonly removalForm = this.formBuilder.nonNullable.group({
    productId: [0, [Validators.min(1)]],
    warehouseId: [0, [Validators.min(1)]]
  });

  readonly selectedCategory = computed(
    () => this.categories().find((category) => category.id === this.productForm.controls.categoryId.value) ?? null
  );

  readonly productFormTitle = computed(() =>
    this.selectedProductId() ? 'Update product' : 'Create product'
  );

  readonly activeFilterPlaceholder = computed(() => {
    const selected = this.filterOptions.find(
      (option) => option.value === this.inventoryFilterForm.controls.filterType.value
    );
    return selected?.placeholder ?? 'Enter a filter value';
  });

  constructor(
    private readonly warehouseService: WarehouseService,
    private readonly productCategoryService: ProductCategoryService,
    private readonly productService: ProductService,
    private readonly inventoryService: InventoryService,
    private readonly inventoryHistoryService: InventoryHistoryService
  ) {}

  ngOnInit(): void {
    this.bootstrapDashboard();
  }

  bootstrapDashboard(): void {
    this.isBootstrapping.set(true);
    this.clearFeedback();

    this.loadWarehouses();
    this.loadProducts();
    this.loadCategories();
    this.loadHistory();

    this.isBootstrapping.set(false);
  }

  loadWarehouses(page = this.currentWarehousePage()): void {
    this.currentWarehousePage.set(page);

    const request: WarehouseRequest = {
      page,
      limit: this.warehousePageSize
    };

    this.warehouseService.getWarehouses(request).subscribe({
      next: (warehouses) => {
        this.warehouses.set(warehouses);
        this.syncWarehouseSelections(warehouses);
      },
      error: () => this.setError('Failed to load warehouses.')
    });
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products.set(products);
        this.syncProductSelections(products);
      },
      error: () => this.setError('Failed to load products.')
    });
  }

  loadCategories(): void {
    this.productCategoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories.set(categories);
        if (!this.productForm.controls.categoryId.value && categories.length > 0) {
          this.productForm.patchValue({ categoryId: categories[0].id });
        }
      },
      error: () => this.setError('Failed to load product categories.')
    });
  }

  loadHistory(page = this.currentHistoryPage()): void {
    this.currentHistoryPage.set(page);
    this.isLoadingHistory.set(true);

    this.inventoryHistoryService
      .getHistory({ page, limit: this.historyPageSize })
      .subscribe({
        next: (history) => {
          this.historyItems.set(history);
          this.isLoadingHistory.set(false);
        },
        error: () => {
          this.isLoadingHistory.set(false);
          this.setError('Failed to load inventory history.');
        }
      });
  }

  submitProduct(): void {
    if (this.productForm.invalid || !this.selectedCategory()) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isSavingProduct.set(true);
    this.clearFeedback();

    const payload = {
      name: this.productForm.controls.name.value.trim(),
      productCategory: this.selectedCategory()!
    };

    const request$ = this.selectedProductId()
      ? this.productService.updateProduct({
          id: this.selectedProductId()!,
          ...payload,
          isDeleted: false
        })
      : this.productService.addProduct(payload);

    request$.subscribe({
      next: () => {
        this.isSavingProduct.set(false);
        this.setMessage(this.selectedProductId() ? 'Product updated.' : 'Product created.');
        this.resetProductForm();
        this.loadProducts();
      },
      error: (error) => {
        this.isSavingProduct.set(false);
        this.setError(this.extractApiError(error, 'Failed to save product.'));
      }
    });
  }

  editProduct(product: Product): void {
    this.selectedProductId.set(product.id);
    this.productForm.patchValue({
      name: product.name,
      categoryId: product.productCategory.id
    });
    this.globalMessage.set(`Editing ${product.name}.`);
    this.globalError.set('');
  }

  resetProductForm(): void {
    this.selectedProductId.set(null);
    this.productForm.reset({
      name: '',
      categoryId: this.categories()[0]?.id ?? 0
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
          this.setError(this.extractApiError(error, 'Failed to load inventory.'));
        }
      });
  }

  submitStockAdjustment(): void {
    if (this.stockAdjustmentForm.invalid) {
      this.stockAdjustmentForm.markAllAsTouched();
      return;
    }

    this.isAdjustingInventory.set(true);
    this.clearFeedback();

    const rawAmount = this.stockAdjustmentForm.controls.amount.value;
    const signedAmount =
      this.stockAdjustmentForm.controls.action.value === 'TAKE' ? -rawAmount : rawAmount;

    this.inventoryService
      .adjustStock({
        productId: this.stockAdjustmentForm.controls.productId.value,
        warehouseId: this.stockAdjustmentForm.controls.warehouseId.value,
        amount: signedAmount
      })
      .subscribe({
        next: () => {
          this.isAdjustingInventory.set(false);
          this.setMessage('Inventory updated.');
          this.searchInventory();
          this.loadHistory(0);
        },
        error: (error) => {
          this.isAdjustingInventory.set(false);
          this.setError(this.extractApiError(error, 'Failed to update inventory.'));
        }
      });
  }

  deleteInventoryItem(productId: number, warehouseId: number): void {
    this.clearFeedback();

    this.inventoryService.deleteInventoryItem({ productId, warehouseId }).subscribe({
      next: () => {
        this.setMessage('Inventory entry removed.');
        this.searchInventory();
      },
      error: (error) => {
        this.setError(this.extractApiError(error, 'Failed to delete inventory entry.'));
      }
    });
  }

  submitRemoval(): void {
    if (this.removalForm.invalid) {
      this.removalForm.markAllAsTouched();
      return;
    }

    this.deleteInventoryItem(
      this.removalForm.controls.productId.value,
      this.removalForm.controls.warehouseId.value
    );
  }

  nextWarehousePage(): void {
    this.loadWarehouses(this.currentWarehousePage() + 1);
  }

  previousWarehousePage(): void {
    if (this.currentWarehousePage() > 0) {
      this.loadWarehouses(this.currentWarehousePage() - 1);
    }
  }

  nextHistoryPage(): void {
    this.loadHistory(this.currentHistoryPage() + 1);
  }

  previousHistoryPage(): void {
    if (this.currentHistoryPage() > 0) {
      this.loadHistory(this.currentHistoryPage() - 1);
    }
  }

  trackById(_: number, item: { id: number }): number {
    return item.id;
  }

  private syncProductSelections(products: Product[]): void {
    if (this.stockAdjustmentForm.controls.productId.value === 0 && products.length > 0) {
      const firstProductId = products[0].id;
      this.stockAdjustmentForm.patchValue({ productId: firstProductId });
      this.removalForm.patchValue({ productId: firstProductId });
    }
  }

  private syncWarehouseSelections(warehouses: Warehouse[]): void {
    if (this.stockAdjustmentForm.controls.warehouseId.value === 0 && warehouses.length > 0) {
      const firstWarehouseId = warehouses[0].id;
      this.stockAdjustmentForm.patchValue({ warehouseId: firstWarehouseId });
      this.removalForm.patchValue({ warehouseId: firstWarehouseId });
    }
  }

  private clearFeedback(): void {
    this.globalMessage.set('');
    this.globalError.set('');
  }

  private setMessage(message: string): void {
    this.globalMessage.set(message);
    this.globalError.set('');
  }

  private setError(message: string): void {
    this.globalError.set(message);
    this.globalMessage.set('');
  }

  private extractApiError(error: unknown, fallback: string): string {
    const candidate = error as { error?: { message?: string } | string };

    if (typeof candidate?.error === 'string' && candidate.error.trim()) {
      return candidate.error;
    }

    if (
      typeof candidate?.error === 'object' &&
      candidate.error !== null &&
      'message' in candidate.error &&
      typeof candidate.error.message === 'string' &&
      candidate.error.message.trim()
    ) {
      return candidate.error.message;
    }

    return fallback;
  }
}
