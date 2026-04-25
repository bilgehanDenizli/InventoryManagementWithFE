import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductCategory } from '../models/product-category.model';

@Injectable({
  providedIn: 'root'
})
export class ProductCategoryService {
  private readonly apiUrl = '/api/productCategory';

  constructor(private readonly http: HttpClient) {}

  getCategories(): Observable<ProductCategory[]> {
    return this.http.post<ProductCategory[]>(`${this.apiUrl}/categories`, {});
  }
}
