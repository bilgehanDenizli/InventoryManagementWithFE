import { Component, OnInit, signal } from "@angular/core";
import { Warehouse } from "../../models/warehouse.model";
import { WarehouseRequest, WarehouseService } from "../../services/warehouse.service";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

@Component({
    selector: 'app-warehouse-list',
    templateUrl: './warehouse-list.component.html',
    styleUrls: ['./warehouse-list.component.css'],
    imports: [CommonModule, RouterModule]
})
export class WarehouseListComponent implements OnInit {
    warehouses = signal<Warehouse[]>([]);
    isLoading = signal(false);
    errorMessage = signal('');

    currentPage = signal(0);
    pageSize: number = 10;
    totalItems: number = 0;

    constructor(private warehouseService: WarehouseService) { }

    ngOnInit(): void {
        this.loadWarehouses();
    }

    private loadWarehouses(): void {
        this.isLoading.set(true);
        const request: WarehouseRequest = {
            page: this.currentPage(),
            limit: this.pageSize
        };
        this.warehouseService.getWarehouses(request).subscribe({
            next: (warehouses: Warehouse[]) => {
                console.log('Warehouses received:', warehouses);
                this.warehouses.set(warehouses);
                this.isLoading.set(false);
            },
            error: (error) => {
                this.errorMessage.set('Failed to load warehouses.');
                this.isLoading.set(false);
                console.error(error);
            }
        });
    }
    nextPage(): void {
        this.currentPage.set(this.currentPage() + 1);
        this.loadWarehouses();
    }

    previousPage(): void {
        if (this.currentPage() > 0) {
            this.currentPage.set(this.currentPage() - 1);
            this.loadWarehouses();
        }
    }

    goToPage(page: number): void {
        this.currentPage.set(page);
        this.loadWarehouses();
    }
}