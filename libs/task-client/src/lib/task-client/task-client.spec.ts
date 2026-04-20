import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskClient } from './task-client';

describe('TaskClient', () => {
  let component: TaskClient;
  let fixture: ComponentFixture<TaskClient>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskClient],
    }).compileComponents();

    fixture = TestBed.createComponent(TaskClient);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
