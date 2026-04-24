import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { ProductCategory } from '../models/product-category.model';

export interface ProductUpsertRequest {
  name: string;
  productCategory: ProductCategory;
}

export interface ProductUpdateRequest extends ProductUpsertRequest {
  id: number;
  isDeleted: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly apiUrl = '/api/product';

  constructor(private readonly http: HttpClient) {}

  getProducts(): Observable<Product[]> {
    return this.http.post<Product[]>(`${this.apiUrl}/products`, {});
  }

  addProduct(request: ProductUpsertRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/add`, request);
  }

  updateProduct(request: ProductUpdateRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/updateProduct`, request);
  }
}
