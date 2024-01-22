import {Injectable, NgZone} from "@angular/core";
import {Observable, of} from "rxjs";
import {HttpClient, provideHttpClient, withFetch} from "@angular/common/http";
import {ITask, ITaskCreateRequest} from "../model/itask";
import { isPlatformBrowser, isPlatformServer } from '@angular/common';
import { Inject, PLATFORM_ID } from '@angular/core';


@Injectable({
  providedIn: 'root'
})
export class TaskService {

  readonly baseUrl;

  constructor(@Inject(PLATFORM_ID) private platformId: Object, private readonly http: HttpClient, private readonly ngZone: NgZone) {
    this.baseUrl = 'http://localhost:8080/tasks';
  }

  ngOnInit(): void {
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
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updateDescription(id: string, version: number, description: string): Observable<any> {
    return this.http.patch<void>(`${this.baseUrl}/${id}`, {description});
  }

  updateStatus(id: string, version: number, status: TaskStatus): Observable<any> {
    return this.http.patch<void>(`${this.baseUrl}/${id}`, {status});
  }



}

export enum TaskStatus {
  TODO = "TODO",
  IN_PROGRESS = "IN_PROGRESS",
  DONE = "DONE"
}
