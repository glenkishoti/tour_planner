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
  popularity?: number;
  childFriendliness?: number;
}

export interface TourRequest {
  name: string;
  description: string;
  from: string;
  to: string;
  transportType: string;
  distance?: number | null;
  estimatedTimeMinutes?: number | null;
}
