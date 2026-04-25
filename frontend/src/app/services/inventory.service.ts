import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Inventory } from '../models/inventory.model';
import { InventoryFilterType } from '../models/filter-type.model';

export interface InventoryQueryRequest {
  filterType: InventoryFilterType;
  filterValue: string;
}

export interface InventoryAdjustmentRequest {
  productId: number;
  warehouseId: number;
  amount: number;
}

export interface InventoryDeleteRequest {
  productId: number;
  warehouseId: number;
}

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private readonly apiUrl = '/api/inventory';

  constructor(private readonly http: HttpClient) {}

  getInventory(request: InventoryQueryRequest): Observable<Inventory[]> {
    return this.http.post<Inventory[]>(`${this.apiUrl}/`, request);
  }

  adjustStock(request: InventoryAdjustmentRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/addOrTakeProduct`, request);
  }

  deleteInventoryItem(request: InventoryDeleteRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/deleteProduct`, request);
  }
}