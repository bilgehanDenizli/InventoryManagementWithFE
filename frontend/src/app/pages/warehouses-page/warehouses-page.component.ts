import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Warehouse } from '../../models/warehouse.model';
import { WarehouseRequest, WarehouseService } from '../../services/warehouse.service';
import { extractApiError } from '../../utils/http-error.util';

@Component({
  selector: 'app-warehouses-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './warehouses-page.component.html',
  styleUrl: './warehouses-page.component.scss'
})
export class WarehousesPageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);

  readonly warehouses = signal<Warehouse[]>([]);
  readonly selectedWarehouse = signal<Warehouse | null>(null);

  readonly currentPage = signal(0);
  readonly pageSize = 10;

  readonly isLoadingList = signal(false);
  readonly isLoadingDetail = signal(false);

  readonly message = signal('');
  readonly error = signal('');

  readonly lookupForm = this.formBuilder.nonNullable.group({
    warehouseId: [0, [Validators.required, Validators.min(1)]]
  });

  readonly cityCount = computed(() => new Set(this.warehouses().map((warehouse) => warehouse.city)).size);
  readonly regionCount = computed(() => new Set(this.warehouses().map((warehouse) => warehouse.region)).size);

  constructor(private readonly warehouseService: WarehouseService) {}

  ngOnInit(): void {
    this.loadWarehouses();
  }

  loadWarehouses(page = this.currentPage()): void {
    this.currentPage.set(page);
    this.isLoadingList.set(true);
    this.clearFeedback();

    const request: WarehouseRequest = {
      page,
      limit: this.pageSize
    };

    this.warehouseService.getWarehouses(request).subscribe({
      next: (warehouses) => {
        this.warehouses.set(warehouses);
        this.isLoadingList.set(false);
      },
      error: (error) => {
        this.isLoadingList.set(false);
        this.setError(extractApiError(error, 'Failed to load warehouses.'));
      }
    });
  }

  lookupWarehouse(): void {
    if (this.lookupForm.invalid) {
      this.lookupForm.markAllAsTouched();
      return;
    }

    this.isLoadingDetail.set(true);
    this.clearFeedback();

    this.warehouseService.getWarehouseById(this.lookupForm.controls.warehouseId.value).subscribe({
      next: (warehouse) => {
        this.selectedWarehouse.set(warehouse);
        this.isLoadingDetail.set(false);
        this.setMessage(`Loaded warehouse ${warehouse.name}.`);
      },
      error: (error) => {
        this.selectedWarehouse.set(null);
        this.isLoadingDetail.set(false);
        this.setError(extractApiError(error, 'Failed to load warehouse detail.'));
      }
    });
  }

  nextPage(): void {
    this.loadWarehouses(this.currentPage() + 1);
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.loadWarehouses(this.currentPage() - 1);
    }
  }

  clearSelection(): void {
    this.selectedWarehouse.set(null);
    this.lookupForm.reset({ warehouseId: 0 });
    this.clearFeedback();
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
