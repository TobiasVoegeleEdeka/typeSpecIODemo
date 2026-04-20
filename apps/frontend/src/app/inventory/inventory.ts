import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService, Product } from '@app/task-client';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    MatCardModule, 
    MatButtonModule, 
    MatFormFieldModule, 
    MatInputModule,
    MatIconModule
  ],
  template: `
    <div class="inventory-container">
      <header class="section-header">
        <h2>Inventory Management</h2>
        <p>Manage your products with ease</p>
      </header>

      <!-- Add Product Form -->
      <mat-card class="add-card">
        <mat-card-header>
          <mat-card-title>Add New Product</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <div class="form-grid">
            <mat-form-field appearance="fill">
              <mat-label>Product Name</mat-label>
              <input matInput [(ngModel)]="newProduct.name" />
            </mat-form-field>
            
            <mat-form-field appearance="fill">
              <mat-label>Description</mat-label>
              <input matInput [(ngModel)]="newProduct.description" />
            </mat-form-field>
            
            <mat-form-field appearance="fill">
              <mat-label>Price (€)</mat-label>
              <input matInput type="number" [(ngModel)]="newProduct.price" />
            </mat-form-field>
            
            <mat-form-field appearance="fill">
              <mat-label>Stock Level</mat-label>
              <input matInput type="number" [(ngModel)]="newProduct.stock" />
            </mat-form-field>
          </div>
        </mat-card-content>
        <mat-card-actions align="end">
          <button mat-raised-button color="primary" (click)="createProduct()" [disabled]="!newProduct.name">
            Create Product
          </button>
        </mat-card-actions>
      </mat-card>

      <!-- Product List -->
      <div class="product-grid">
        <mat-card *ngFor="let p of products()" class="product-card">
          <mat-card-header>
            <mat-card-title>{{ p.name }}</mat-card-title>
            <mat-card-subtitle>{{ p.description }}</mat-card-subtitle>
          </mat-card-header>
          <mat-card-content>
            <div class="product-stats">
              <span class="price-tag">{{ p.price | currency:'EUR' }}</span>
              <span class="stock-tag" [class.low-stock]="p.stock < 10">
                {{ p.stock }} in stock
              </span>
            </div>
          </mat-card-content>
          <mat-card-actions>
            <button mat-button color="warn" (click)="deleteProduct(p.id)">Delete</button>
          </mat-card-actions>
        </mat-card>
      </div>
    </div>
  `,
  styles: [`
    .inventory-container {
      padding: 16px;
    }
    .section-header {
      margin-bottom: 32px;
    }
    .section-header h2 {
      margin: 0;
      font-size: 2rem;
      color: var(--app-primary);
    }
    .add-card {
      margin-bottom: 40px;
    }
    .form-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
      margin-top: 16px;
    }
    .product-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 24px;
    }
    .product-stats {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 16px;
    }
    .price-tag {
      font-size: 1.25rem;
      font-weight: 600;
      color: var(--app-accent);
    }
    .stock-tag {
      font-size: 0.875rem;
      padding: 4px 12px;
      background: #eee;
      border-radius: 16px;
    }
    .stock-tag.low-stock {
      background: #ffebee;
      color: #c62828;
    }
  `]
})
export class InventoryComponent implements OnInit {
  private inventoryService = inject(InventoryService);
  
  products = signal<Product[]>([]);
  newProduct: Partial<Product> = { name: '', description: '', price: 0, stock: 0 };

  ngOnInit() {
    this.loadInventory();
  }

  loadInventory() {
    this.inventoryService.inventoryList().subscribe(data => {
      this.products.set(data);
    });
  }

  createProduct() {
    if (!this.newProduct.name || this.newProduct.price === undefined || this.newProduct.stock === undefined) return;
    
    this.inventoryService.inventoryCreate(this.newProduct as Product).subscribe(() => {
      this.loadInventory();
      this.newProduct = { name: '', description: '', price: 0, stock: 0 };
    });
  }

  deleteProduct(id: string) {
    this.inventoryService.inventoryDelete(id).subscribe(() => {
      this.loadInventory();
    });
  }
}
