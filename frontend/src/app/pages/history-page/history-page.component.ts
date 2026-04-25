import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
import { InventoryHistory } from '../../models/inventory-history.model';
import { InventoryHistoryService } from '../../services/inventory-history.service';
import { extractApiError } from '../../utils/http-error.util';

@Component({
  selector: 'app-history-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history-page.component.html',
  styleUrl: './history-page.component.scss'
})
export class HistoryPageComponent implements OnInit {
  readonly historyItems = signal<InventoryHistory[]>([]);

  readonly currentPage = signal(0);
  readonly pageSize = 10;

  readonly isLoading = signal(false);
  readonly isGenerating = signal(false);

  readonly message = signal('');
  readonly error = signal('');

  readonly positiveCount = computed(() =>
    this.historyItems().filter((item) => item.amountChange > 0).length
  );

  readonly negativeCount = computed(() =>
    this.historyItems().filter((item) => item.amountChange < 0).length
  );

  readonly netChange = computed(() =>
    this.historyItems().reduce((sum, item) => sum + item.amountChange, 0)
  );

  constructor(private readonly inventoryHistoryService: InventoryHistoryService) {}

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(page = this.currentPage()): void {
    this.currentPage.set(page);
    this.isLoading.set(true);
    this.clearFeedback();

    this.inventoryHistoryService
      .getHistory({ page, limit: this.pageSize })
      .subscribe({
        next: (history) => {
          this.historyItems.set(history);
          this.isLoading.set(false);
        },
        error: (error) => {
          this.error.set(extractApiError(error, 'Failed to load inventory history.'));
          this.isLoading.set(false);
        }
      });
  }

  generateBatch(): void {
    this.isGenerating.set(true);
    this.clearFeedback();

    this.inventoryHistoryService.triggerBatchInsert().subscribe({
      next: () => {
        this.isGenerating.set(false);
        this.message.set('Batch insert event sent. Refreshing history...');
        setTimeout(() => this.loadHistory(0), 1200);
      },
      error: (error) => {
        this.isGenerating.set(false);
        this.error.set(extractApiError(error, 'Failed to trigger batch insert.'));
      }
    });
  }

  nextPage(): void {
    this.loadHistory(this.currentPage() + 1);
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.loadHistory(this.currentPage() - 1);
    }
  }

  private clearFeedback(): void {
    this.message.set('');
    this.error.set('');
  }
}
