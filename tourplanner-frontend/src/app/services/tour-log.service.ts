import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TourLog, TourLogRequest } from '../models/tour-log';

@Injectable({ providedIn: 'root' })
export class TourLogService {
  private baseUrl = 'http://localhost:8080/api/tours';

  constructor(private http: HttpClient) {}

  getAll(tourId: number): Observable<TourLog[]> {
    return this.http.get<TourLog[]>(`${this.baseUrl}/${tourId}/logs`);
  }

  create(tourId: number, log: TourLogRequest): Observable<TourLog> {
    return this.http.post<TourLog>(`${this.baseUrl}/${tourId}/logs`, log);
  }

  update(tourId: number, logId: number, log: TourLogRequest): Observable<TourLog> {
    return this.http.put<TourLog>(`${this.baseUrl}/${tourId}/logs/${logId}`, log);
  }

  delete(tourId: number, logId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${tourId}/logs/${logId}`);
  }
}
