import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Inventory } from "../models/inventory.model";
import { inventoryRequest } from "../models/inventory-filter-request.model";

@Injectable({
    providedIn: 'root'
})
export class InventoryService {
    private apiUrl = 'http://localhost:8080/api/inventory';
    constructor(private http: HttpClient) { }

     getInventoryItems(request?: inventoryRequest) {
        return this.http.post<Inventory[]>(this.apiUrl + '/', request || {})
     }

}