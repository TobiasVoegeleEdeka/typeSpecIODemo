import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TasksService, Task } from '@app/task-client';
import { AuthService } from './services/auth.service';
import { InventoryComponent } from './inventory/inventory';
import { DocsComponent } from './docs/docs';
import { Observable } from 'rxjs';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';

@Component({
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    InventoryComponent,
    MatToolbarModule,
    MatTabsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    DocsComponent
  ],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class AppComponent implements OnInit {
  private tasksService = inject(TasksService);
  public authService = inject(AuthService);
  
  currentView = signal<'tasks' | 'inventory' | 'docs'>('tasks');
  
  tasks$: Observable<Task[]> | undefined;
  
  credentials = {
    username: '',
    password: ''
  };

  loginError = signal<string | null>(null);

  ngOnInit() {
    this.loadTasks();
  }

  loadTasks() {
    this.tasks$ = this.tasksService.tasksList();
  }

  login() {
    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.loginError.set(null);
        this.loadTasks();
      },
      error: () => {
        this.loginError.set('Invalid credentials');
      }
    });
  }

  logout() {
    this.authService.logout();
  }

  toggleComplete(task: Task) {
    if (!this.authService.hasPermission('UPDATE_TASKS')) {
      alert('You do not have permission to update tasks');
      return;
    }

    this.tasksService.tasksUpdate(task.id, { ...task, completed: !task.completed }).subscribe(() => {
      this.loadTasks();
    });
  }

  deleteTask(task: Task) {
    if (!this.authService.hasPermission('DELETE_TASKS')) {
      alert('You do not have permission to delete tasks');
      return;
    }

    this.tasksService.tasksDelete(task.id).subscribe(() => {
      this.loadTasks();
    });
  }
}
