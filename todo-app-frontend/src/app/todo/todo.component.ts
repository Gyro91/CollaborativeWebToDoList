import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ITask } from "../model/task";
import { CdkDragDrop, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";

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

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.todoForm = this.formBuilder.group({
      item: ['', Validators.required]
    });
  }

  onSubmitAddTask(): void {
    if (this.todoForm.valid) {
      this.addTask(this.todoForm.value.item);
      this.todoForm.reset();
    }
  }

  addTask(item: string) {
    this.tasks.push({ description: item, done: false });
  }

  onSubmitUpdateTask(): void {
    if (this.todoForm.valid) {
      this.tasks[this.updateIndex].description = this.todoForm.value.item;
      this.tasks[this.updateIndex].done = false;
    }
    this.todoForm.reset();
    this.isEditEnabled = false;
    this.updateIndex = undefined;
  }

  onEdit(item: ITask, i: number) {
    this.todoForm.controls['item'].setValue(item.description)
    this.updateIndex = i;
    this.isEditEnabled = true;
  }

  deleteTask(i: number, status: String) {
    if (status == "TO DO") {
      this.tasks.splice(i, 1)
    } else if (status == "IN PROGRESS") {
      this.inprogress.splice(i, 1)
    } else if (status == "DONE") {
      this.done.splice(i, 1)
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
    }
  }
}
