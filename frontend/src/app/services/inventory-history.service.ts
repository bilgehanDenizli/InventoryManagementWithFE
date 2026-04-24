import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InventoryHistory } from '../models/inventory-history.model';

export interface HistoryRequest {
  page: number;
  limit: number;
}

@Injectable({
  providedIn: 'root'
})
export class InventoryHistoryService {
  private readonly apiUrl = '/api/inventoryHistory';

  constructor(private readonly http: HttpClient) {}

  getHistory(request: HistoryRequest): Observable<InventoryHistory[]> {
    return this.http.post<InventoryHistory[]>(`${this.apiUrl}/`, request);
  }
}
