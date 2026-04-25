import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Product } from '../../models/product.model';
import { ProductCategory } from '../../models/product-category.model';
import { ProductCategoryService } from '../../services/product-category.service';
import { ProductService } from '../../services/product.service';
import { extractApiError } from '../../utils/http-error.util';

@Component({
  selector: 'app-products-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './products-page.component.html',
  styleUrl: './products-page.component.scss'
})
export class ProductsPageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);

  readonly products = signal<Product[]>([]);
  readonly categories = signal<ProductCategory[]>([]);
  readonly productDetail = signal<Product | null>(null);
  readonly selectedProductId = signal<number | null>(null);

  readonly isLoadingProducts = signal(false);
  readonly isLoadingDetail = signal(false);
  readonly isSaving = signal(false);
  readonly isArchiving = signal(false);

  readonly message = signal('');
  readonly error = signal('');

  readonly productForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    categoryId: [0, [Validators.required, Validators.min(1)]]
  });

  readonly lookupForm = this.formBuilder.nonNullable.group({
    productId: [0, [Validators.required, Validators.min(1)]]
  });

  readonly selectedCategory = computed(
    () => this.categories().find((category) => category.id === this.productForm.controls.categoryId.value) ?? null
  );

  readonly productFormTitle = computed(() =>
    this.selectedProductId() ? 'Update product' : 'Create product'
  );

  constructor(
    private readonly productService: ProductService,
    private readonly productCategoryService: ProductCategoryService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts(): void {
    this.isLoadingProducts.set(true);
    this.clearFeedback();

    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products.set(products);
        this.isLoadingProducts.set(false);
      },
      error: (error) => {
        this.isLoadingProducts.set(false);
        this.setError(extractApiError(error, 'Failed to load products.'));
      }
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
      error: (error) => this.setError(extractApiError(error, 'Failed to load product categories.'))
    });
  }

  submitProduct(): void {
    if (this.productForm.invalid || !this.selectedCategory()) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
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
        this.isSaving.set(false);
        this.setMessage(this.selectedProductId() ? 'Product updated.' : 'Product created.');
        this.resetForm();
        this.loadProducts();
      },
      error: (error) => {
        this.isSaving.set(false);
        this.setError(extractApiError(error, 'Failed to save product.'));
      }
    });
  }

  editProduct(product: Product): void {
    this.selectedProductId.set(product.id);
    this.productForm.patchValue({
      name: product.name,
      categoryId: product.productCategory.id
    });
    this.productDetail.set(product);
    this.setMessage(`Editing ${product.name}.`);
  }

  archiveProduct(product: Product): void {
    this.isArchiving.set(true);
    this.clearFeedback();

    this.productService
      .updateProduct({
        id: product.id,
        name: product.name,
        productCategory: product.productCategory,
        isDeleted: true
      })
      .subscribe({
        next: () => {
          this.isArchiving.set(false);
          this.setMessage(`Archived ${product.name}.`);
          this.resetForm();
          this.loadProducts();
        },
        error: (error) => {
          this.isArchiving.set(false);
          this.setError(extractApiError(error, 'Failed to archive product.'));
        }
      });
  }

  lookupProduct(): void {
    if (this.lookupForm.invalid) {
      this.lookupForm.markAllAsTouched();
      return;
    }

    this.isLoadingDetail.set(true);
    this.clearFeedback();

    this.productService.getProductById(this.lookupForm.controls.productId.value).subscribe({
      next: (product) => {
        this.productDetail.set(product);
        this.isLoadingDetail.set(false);
        this.setMessage(`Loaded product ${product.name}.`);
      },
      error: (error) => {
        this.productDetail.set(null);
        this.isLoadingDetail.set(false);
        this.setError(extractApiError(error, 'Failed to load product detail.'));
      }
    });
  }

  resetForm(): void {
    this.selectedProductId.set(null);
    this.productForm.reset({
      name: '',
      categoryId: this.categories()[0]?.id ?? 0
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
