import { Injectable } from "@angular/core"
import { map, Observable } from "rxjs"
import { Warehouse } from "../models/warehouse.model"
import { HttpClient } from "@angular/common/http"

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
    private apiUrl = 'http://localhost:8080/api/warehouse';
    constructor(private http: HttpClient) { }

    getWarehouses(request?: WarehouseRequest): Observable<Warehouse[]> {
        return this.http.post<Warehouse[]>(this.apiUrl + '/', request || {});
    }
}
