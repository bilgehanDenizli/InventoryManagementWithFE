import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterModule } from "@angular/router";
import { FilterType } from "../../models/filter-type.enum";
import { inventoryRequest } from "../../models/inventory-filter-request.model";
import { InventoryService } from "../../services/inventory.service";
import { FormsModule } from "@angular/forms";


@Component({
    selector: 'app-inventory-items-search',
    templateUrl: './inventory-items-search.component.html',
    styleUrls: ['./inventory-items-search.component.css'],
    imports: [CommonModule, RouterModule, FormsModule]
})
export class InventoryItemsSearchComponent {
    filterTypes = Object.values(FilterType);
    filterRequest: inventoryRequest = {
    filterType: FilterType.WAREHOUSE_NAME,
    filterValue: ''
  };

  constructor(private inventoryService: InventoryService) { }

  submit() {
    console.log('Sending request:', this.filterRequest);
    this.inventoryService.getInventoryItems(this.filterRequest)
  }
}