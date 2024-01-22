import {TaskStatus} from "../service/task.service";

export interface ITask {
  id: string;
  version: number;
  description: string;
  status: TaskStatus;
  createdDate: string;
  lastModifiedDate: string;
}

export interface ITaskCreateRequest {
  description: string;
}
