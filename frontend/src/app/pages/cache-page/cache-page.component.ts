import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CacheService } from '../../services/cache.service';
import { extractApiError } from '../../utils/http-error.util';

@Component({
  selector: 'app-cache-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cache-page.component.html',
  styleUrl: './cache-page.component.scss'
})
export class CachePageComponent {
  private readonly formBuilder = inject(FormBuilder);

  readonly cacheNames = [
    'InventoryByWarehouseName',
    'InventoryByWarehouseCity',
    'InventoryByWarehouseRegion',
    'InventoryByProductCategory',
    'InventoryByProductId',
    'InventoryByProductName'
  ];

  readonly isFlushingAll = signal(false);
  readonly isFlushingOne = signal(false);

  readonly message = signal('');
  readonly error = signal('');

  readonly cacheForm = this.formBuilder.nonNullable.group({
    cacheName: [this.cacheNames[0], [Validators.required]]
  });

  constructor(private readonly cacheService: CacheService) {}

  flushAll(): void {
    this.isFlushingAll.set(true);
    this.clearFeedback();

    this.cacheService.flushAll().subscribe({
      next: () => {
        this.isFlushingAll.set(false);
        this.message.set('All inventory caches flushed.');
      },
      error: (error) => {
        this.isFlushingAll.set(false);
        this.error.set(extractApiError(error, 'Failed to flush all caches.'));
      }
    });
  }

  flushByName(): void {
    if (this.cacheForm.invalid) {
      this.cacheForm.markAllAsTouched();
      return;
    }

    this.isFlushingOne.set(true);
    this.clearFeedback();

    const cacheName = this.cacheForm.controls.cacheName.value;

    this.cacheService.flushByName(cacheName).subscribe({
      next: () => {
        this.isFlushingOne.set(false);
        this.message.set(`${cacheName} flushed.`);
      },
      error: (error) => {
        this.isFlushingOne.set(false);
        this.error.set(extractApiError(error, 'Failed to flush cache.'));
      }
    });
  }

  private clearFeedback(): void {
    this.message.set('');
    this.error.set('');
  }
}
