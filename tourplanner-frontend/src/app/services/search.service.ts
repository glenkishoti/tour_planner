import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tour } from '../models/tour';
import { TourLog } from '../models/tour-log';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private baseUrl = '/api/search';

  constructor(private http: HttpClient) {}

  searchTours(query: string): Observable<Tour[]> {
    return this.http.get<Tour[]>(`${this.baseUrl}/tours`, {
      params: { query }
    });
  }

  searchTourLogs(query: string): Observable<TourLog[]> {
    return this.http.get<TourLog[]>(`${this.baseUrl}/tour-logs`, {
      params: { query }
    });
  }
}
