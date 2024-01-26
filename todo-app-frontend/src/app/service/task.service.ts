import {Injectable, NgZone} from "@angular/core";
import {catchError, Observable, of, throwError} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ITask, ITaskCreateRequest} from "../model/itask";
import { isPlatformBrowser } from '@angular/common';
import { Inject, PLATFORM_ID } from '@angular/core';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class TaskService {

  readonly baseUrl = environment.baseUrl;

  constructor(@Inject(PLATFORM_ID) private platformId: Object, private readonly http: HttpClient, private readonly ngZone: NgZone) {
    console.log(this.baseUrl);
  }

  findAll(): Observable<ITask> {
    if (isPlatformBrowser(this.platformId)) {
      return new Observable<ITask>((subscriber) => {
        const eventSource = new EventSource(this.baseUrl);

        // Process incoming messages
        eventSource.onmessage = (event: MessageEvent) => {
          const item = JSON.parse(event.data);
          this.ngZone.run(() => subscriber.next(item));
        };

        // Handle errors
        eventSource.onerror = (error: Event) => {
          if (eventSource.readyState === 0) {
            eventSource.close();
            subscriber.complete();
          } else {
            subscriber.error(error);
          }
        };
      });
    } else {
      return of();
    }
  }

  addTask(requestBody: ITaskCreateRequest): Observable<ITask> {
    return this.http.post<ITask>(this.baseUrl, requestBody);
  }

  findById(id: string): Observable<ITask> {
    return this.http.get<ITask>(`${this.baseUrl}/${id}`);
  }

  delete(id: string, version: number): Observable<any> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, TaskService.buildOptions(version)).pipe(
      catchError(err => {
        const message = `Error: ${err.status} - ${err.statusText || ''} ${err.error?.message || err.message}`;
        return throwError(() => new Error(message));
      })
    );
  }

  updateDescription(id: string, version: number, description: string): Observable<any> {
    return this.http.patch<void>(`${this.baseUrl}/${id}`, {description}, TaskService.buildOptions(version)).pipe(
      catchError(err => {
        const message = `Error: ${err.status} - ${err.statusText || ''} ${err.error?.message || err.message}`;
        return throwError(() => new Error(message));
      })
    );
  }

  updateStatus(id: string, version: number, status: TaskStatus): Observable<any> {
    return this.http.patch<void>(`${this.baseUrl}/${id}`, {status}, TaskService.buildOptions(version)).pipe(
      catchError(err => {
        const message = `Error: ${err.status} - ${err.statusText || ''} ${err.error?.message || err.message}`;
        return throwError(() => new Error(message));
      })
    );
  }

  listenToEvents(onSaved: (task: ITask) => void, onDeleted: (taskId: string) => void): EventSource | null {
    if (isPlatformBrowser(this.platformId)) {
      const eventSource = new EventSource(`${this.baseUrl}/events`);

      // Handle the creation and the update of items
      eventSource.addEventListener('TaskSavedEvent', (event: MessageEvent) => {
        console.log(event.data);
        const task: ITask = JSON.parse(event.data).taskResource;
        onSaved(task);
      });

      // Handle the deletion of items
      eventSource.addEventListener('TaskDeletedEvent', (event: MessageEvent) => {
        let taskIdObject = JSON.parse(event.data);
        let taskId = taskIdObject.taskId;
        onDeleted(taskId);
      });

      // Handle errors
      eventSource.onerror = (error: Event) => {
        if (eventSource.readyState === 0) {
          console.error('Stream closed');
        } else {
          console.error(error);
        }
      };
      return eventSource;
    } else {
      // Handle non-browser environments
      return null;
    }
  }

  private static buildOptions(version: number) {
    return {
      headers: new HttpHeaders({
        'if-match': String(version)
      })
    };
  }

}

export enum TaskStatus {
  TODO = "TODO",
  IN_PROGRESS = "IN_PROGRESS",
  DONE = "DONE"
}
