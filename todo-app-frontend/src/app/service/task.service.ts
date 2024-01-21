import {Injectable, NgZone} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, provideHttpClient, withFetch} from "@angular/common/http";
import {ITask} from "../model/itask";

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  readonly baseUrl;

  constructor(private readonly http: HttpClient, private readonly ngZone: NgZone) {
    this.baseUrl = 'http://localhost:8080/tasks';
  }

  ngOnInit(): void {
    provideHttpClient(withFetch());
  }

  findAll(): Observable<ITask> {
    return new Observable<ITask>((subscriber) => {

      const eventSource = new EventSource(this.baseUrl);

      // Process incoming messages
      eventSource.onmessage = (event) => {
        const item = JSON.parse(event.data);
        this.ngZone.run(()=>subscriber.next(item));
      };

      // Handle errors
      eventSource.onerror = (error) => {
        if (eventSource.readyState === 0) {
          // The connection has been closed by the server
          eventSource.close();
          subscriber.complete();
        } else {
          subscriber.error(error);
        }
      };
    });
  }

  addTask(description: ITask): Observable<ITask> {
    return this.http.post<ITask>(this.baseUrl, {description});
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
