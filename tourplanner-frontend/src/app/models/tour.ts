export interface Tour {
  id: number;
  name: string;
  description: string;
  from: string;
  to: string;
  transportType: string;
  distance: number;
  estimatedTimeMinutes: number;
  imagePath?: string;
}

export interface TourRequest {
  name: string;
  description: string;
  from: string;
  to: string;
  transportType: string;
  distance: number;
  estimatedTimeMinutes: number;
}
