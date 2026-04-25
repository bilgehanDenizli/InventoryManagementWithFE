import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
import { ProductCategory } from '../../models/product-category.model';
import { ProductCategoryService } from '../../services/product-category.service';
import { extractApiError } from '../../utils/http-error.util';

@Component({
  selector: 'app-categories-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './categories-page.component.html',
  styleUrl: './categories-page.component.scss'
})
export class CategoriesPageComponent implements OnInit {
  readonly categories = signal<ProductCategory[]>([]);
  readonly isLoading = signal(false);
  readonly error = signal('');

  readonly longestName = computed(() =>
    this.categories().reduce((longest, category) => {
      return category.category.length > longest.length ? category.category : longest;
    }, '')
  );

  constructor(private readonly productCategoryService: ProductCategoryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading.set(true);
    this.error.set('');

    this.productCategoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories.set(categories);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.error.set(extractApiError(error, 'Failed to load categories.'));
        this.isLoading.set(false);
      }
    });
  }
}
