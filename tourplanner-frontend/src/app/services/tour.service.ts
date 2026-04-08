import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tour, TourRequest } from '../models/tour';

@Injectable({ providedIn: 'root' })
export class TourService {
  private baseUrl = 'http://localhost:8080/api/tours';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Tour[]> {
    return this.http.get<Tour[]>(this.baseUrl);
  }

  getById(id: number): Observable<Tour> {
    return this.http.get<Tour>(`${this.baseUrl}/${id}`);
  }

  create(tour: TourRequest): Observable<Tour> {
    return this.http.post<Tour>(this.baseUrl, tour);
  }

  update(id: number, tour: TourRequest): Observable<Tour> {
    return this.http.put<Tour>(`${this.baseUrl}/${id}`, tour);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
