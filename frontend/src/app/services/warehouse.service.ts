import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Warehouse } from '../models/warehouse.model';
import { HttpClient } from '@angular/common/http';

export interface WarehouseRequest {
    page: number;
    limit: number;
}

export interface WarehouseResponse {
  data: Warehouse[];
  total: number;
  page: number;
  limit: number;
}

@Injectable({
  providedIn: 'root'
})
export class WarehouseService {
  private readonly apiUrl = '/api/warehouse';

  constructor(private readonly http: HttpClient) {}

  getWarehouses(request?: WarehouseRequest): Observable<Warehouse[]> {
    return this.http.post<Warehouse[]>(`${this.apiUrl}/`, request || {});
  }
}
