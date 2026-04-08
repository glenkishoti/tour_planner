export interface TourLog {
  id: number;
  dateTime: string;
  comment: string;
  difficulty: number;
  totalDistance: number;
  totalTimeMinutes: number;
  rating: number;
  tourId: number;
}

export interface TourLogRequest {
  dateTime: string;
  comment: string;
  difficulty: number;
  totalDistance: number;
  totalTimeMinutes: number;
  rating: number;
}
