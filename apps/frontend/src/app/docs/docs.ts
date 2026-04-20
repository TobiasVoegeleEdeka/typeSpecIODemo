import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentsService, Document } from '@app/task-client';
import { MarkdownModule } from 'ngx-markdown';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-docs',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    MarkdownModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatDividerModule,
    MatIconModule
  ],
  template: `
    <div class="docs-container">
      <div class="sidebar">
        <mat-card class="list-card">
          <mat-card-header>
            <mat-card-title>Documentation</mat-card-title>
          </mat-card-header>
          <mat-list>
            <mat-list-item *ngFor="let doc of documents()" (click)="selectDoc(doc)" [class.selected]="selectedDoc()?.id === doc.id">
              <span matListItemTitle>{{ doc.name }}</span>
              <span matListItemLine>{{ doc.createdAt | date:'short' }}</span>
              <button mat-icon-button matListItemMeta (click)="deleteDoc(doc.id); $event.stopPropagation()">
                <mat-icon color="warn">delete</mat-icon>
              </button>
            </mat-list-item>
          </mat-list>
          <mat-divider></mat-divider>
          <div class="actions">
            <button mat-flat-button color="accent" (click)="selectedDoc.set(null); isEditing.set(true)">+ New Document</button>
          </div>
        </mat-card>
      </div>

      <div class="main-docs">
        <ng-container *ngIf="isEditing(); else viewerTpl">
          <mat-card class="editor-card">
            <mat-card-header>
              <mat-card-title>Create / Edit Document</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              <mat-form-field appearance="fill" class="full-width">
                <mat-label>Document Name</mat-label>
                <input matInput [(ngModel)]="editBuffer.name" />
              </mat-form-field>
              
              <mat-form-field appearance="fill" class="full-width">
                <mat-label>Markdown Content</mat-label>
                <textarea matInput rows="15" [(ngModel)]="editBuffer.content"></textarea>
              </mat-form-field>
            </mat-card-content>
            <mat-card-actions align="end">
              <button mat-button (click)="isEditing.set(false)">Cancel</button>
              <button mat-raised-button color="primary" (click)="saveDoc()" [disabled]="!editBuffer.name || !editBuffer.content">Save & Render</button>
            </mat-card-actions>
          </mat-card>
        </ng-container>

        <ng-template #viewerTpl>
          <mat-card *ngIf="selectedDoc() as doc; else emptyTpl" class="viewer-card slide-in">
            <mat-card-header>
              <mat-card-title>{{ doc.name }}</mat-card-title>
              <mat-card-subtitle>Created at: {{ doc.createdAt | date:'medium' }}</mat-card-subtitle>
            </mat-card-header>
            <mat-divider></mat-divider>
            <mat-card-content class="markdown-body">
              <markdown [data]="doc.content"></markdown>
            </mat-card-content>
          </mat-card>
          
          <ng-template #emptyTpl>
            <div class="empty-state fade-in">
              <mat-icon>description</mat-icon>
              <p>Select a document to view or create a new one.</p>
            </div>
          </ng-template>
        </ng-template>
      </div>
    </div>
  `,
  styles: [`
    .docs-container { display: grid; grid-template-columns: 300px 1fr; gap: 24px; padding: 20px; }
    .list-card { height: calc(100vh - 120px); display: flex; flex-direction: column; }
    mat-list { flex-grow: 1; overflow-y: auto; }
    mat-list-item { cursor: pointer; transition: 0.2s; border-radius: 8px; margin-bottom: 4px; }
    mat-list-item:hover { background: rgba(0,0,0,0.05); }
    mat-list-item.selected { background: rgba(217, 119, 6, 0.1); color: #B45309; }
    .actions { padding: 16px; text-align: center; }
    .full-width { width: 100%; margin-top: 10px; }
    .markdown-body { padding: 24px 0; }
    .empty-state { text-align: center; padding: 100px 0; color: #999; }
    .empty-state mat-icon { font-size: 64px; width: 64px; height: 64px; opacity: 0.3; }
    .viewer-card { min-height: 400px; }
    .editor-card { position: sticky; top: 0; }
  `]
})
export class DocsComponent implements OnInit {
  private docsService = inject(DocumentsService);
  
  documents = signal<Document[]>([]);
  selectedDoc = signal<Document | null>(null);
  isEditing = signal(false);
  
  editBuffer = { name: '', content: '' };

  ngOnInit() {
    this.loadDocs();
  }

  loadDocs() {
    this.docsService.documentsList().subscribe(data => {
      this.documents.set(data);
    });
  }

  selectDoc(doc: Document) {
    this.selectedDoc.set(doc);
    this.isEditing.set(false);
  }

  saveDoc() {
    this.docsService.documentsCreate(this.editBuffer as Document).subscribe(newDoc => {
      this.loadDocs();
      this.selectDoc(newDoc);
      this.editBuffer = { name: '', content: '' };
    });
  }

  deleteDoc(id: number) {
    this.docsService.documentsDelete(id).subscribe(() => {
      this.loadDocs();
      if (this.selectedDoc()?.id === id) {
        this.selectedDoc.set(null);
      }
    });
  }
}
