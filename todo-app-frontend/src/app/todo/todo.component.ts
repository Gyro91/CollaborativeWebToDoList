import {Component, NgZone, OnInit} from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import {ITask, ITaskCreateRequest} from "../model/itask";
import { CdkDragDrop, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import {TaskService, TaskStatus} from "../service/task.service";

@Component({
  selector: 'app-todo',
  templateUrl: './todo.component.html',
  styleUrls: ['./todo.component.css']
})
export class TodoComponent implements OnInit {

  todoForm !: FormGroup;
  tasks: ITask[] = [];
  inprogress: ITask[] = [];
  done: ITask[] = [];
  updateIndex !: any;
  isEditEnabled : boolean = false;

  constructor(private formBuilder: FormBuilder, private taskService: TaskService, private readonly ngZone: NgZone) {


  }

  ngOnInit(): void {
    this.initializeForm();
    this.fetchTasks();
    this.listenToTaskEvents();
  }

  initializeForm(): void {
    this.todoForm = this.formBuilder.group({
      item: ['', Validators.required]
    });
  }

  fetchTasks(): void {
    this.taskService.findAll().subscribe(task => {
      this.ngZone.run(() => {
        switch (task.status) {
          case TaskStatus.TODO:
            this.tasks.push(task);
            break;
          case TaskStatus.IN_PROGRESS:
            this.inprogress.push(task);
            break;
          case TaskStatus.DONE:
            this.done.push(task);
            break;
        }
      });
    }, error => {
      console.error('Error receiving tasks:', error);
    });
  }

  private listenToTaskEvents(): void {
    this.taskService.listenToEvents(
      (task) => this.handleTaskSaved(task),
      (taskId) => this.handleTaskDeleted(taskId)
    );
  }

  private handleTaskSaved(task: ITask): void {
    this.ngZone.run(() => {
      // Update or add the task in the appropriate list
      console.log("Updating or adding task " + task.id);
      this.addOrUpdateTask(task);
    });
  }

  private addOrUpdateTask(task: ITask): void {
    // Remove the task from all lists if it exists
    this.tasks = this.tasks.filter(t => t.id !== task.id);
    this.inprogress = this.inprogress.filter(t => t.id !== task.id);
    this.done = this.done.filter(t => t.id !== task.id);

    // Add or re-add the task to the appropriate list based on its status
    let targetList = this.getTaskListByStatus(task.status);
    targetList.push(task);
  }

  private getTaskListByStatus(status: TaskStatus): ITask[] {
    switch (status) {
      case TaskStatus.TODO:
        return this.tasks;
      case TaskStatus.IN_PROGRESS:
        return this.inprogress;
      case TaskStatus.DONE:
        return this.done;
      default:
        throw new Error(`Unknown task status: ${status}`);
    }
  }



  private handleTaskDeleted(taskId: string): void {
    this.ngZone.run(() => {
      console.log('Deleting task with ID:', taskId);
      this.deleteTaskById(taskId);
    });
  }

  deleteTaskById(taskId: String): void {
    this.tasks = this.tasks.filter(task => task.id !== taskId);
    this.inprogress = this.inprogress.filter(task => task.id !== taskId);
    this.done = this.done.filter(task => task.id !== taskId);
  }

  onSubmitAddTask(): void {
    if (this.todoForm.valid) {
      this.addTask(this.todoForm.value.item);
      this.todoForm.reset();
    }
  }

  addTask(description: string) {
    const requestBody: ITaskCreateRequest = { description };
    this.taskService.addTask(requestBody).subscribe(task => {
      this.tasks.push(task);
      console.log(task.id);
    });
  }

  onSubmitUpdateTask(): void {
    if (this.todoForm.valid) {
      const updatedTask = {
        ...this.tasks[this.updateIndex],
        description: this.todoForm.value.item
      };
      this.taskService.updateDescription(updatedTask.id, updatedTask.version, updatedTask.description)
        .subscribe({
          next: () => {
            this.tasks[this.updateIndex] = updatedTask;
            this.todoForm.reset();
            this.isEditEnabled = false;
            this.updateIndex = undefined;
          },
          error: (error) => {
            this.displayError(error.message);
          }
        });
    }
  }

  onEdit(item: ITask, i: number) {
    this.todoForm.controls['item'].setValue(item.description)
    this.updateIndex = i;
    this.isEditEnabled = true;
  }

  deleteTask(i: number, status: String) {
    let taskToDelete;
    if (status === 'TO DO') {
      taskToDelete = this.tasks[i];
      this.tasks.splice(i, 1);
    } else if (status === 'IN PROGRESS') {
      taskToDelete = this.inprogress[i];
      this.inprogress.splice(i, 1);
    } else if (status === 'DONE') {
      taskToDelete = this.done[i];
      this.done.splice(i, 1);
    }

    if (taskToDelete) {
      this.taskService.delete(taskToDelete.id, taskToDelete.version).subscribe({
        error: (err) => {
          this.displayError(err.message);
        }
      });
    }
  }

  drop(event: CdkDragDrop<ITask[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );

      // Get the task that was moved
      const task = event.container.data[event.currentIndex];

      // Initialize newStatus with a default or undefined value
      let newStatus: TaskStatus | undefined;
      // Determine the new status based on the container it was moved to
      if (event.container.id === 'todoList') {
        newStatus = TaskStatus.TODO;
      } else if (event.container.id === 'inProgressList') {
        newStatus = TaskStatus.IN_PROGRESS;
      } else if (event.container.id === 'doneList') {
        newStatus = TaskStatus.DONE;
      }

      // Call the updateStatus service method if newStatus is defined
      if (newStatus !== undefined) {
        console.log(task.id);
        this.taskService.updateStatus(task.id, task.version, newStatus)
          .subscribe(
            {
              error: (err) => {
                this.displayError(err.message);
              }
            });
      }
    }
  }

  displayError(message: string): void {
    alert(message);
  }
}
