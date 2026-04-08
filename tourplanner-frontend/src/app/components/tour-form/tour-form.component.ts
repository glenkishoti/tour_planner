import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TourService } from '../../services/tour.service';
import { Tour, TourRequest } from '../../models/tour';

@Component({
  selector: 'app-tour-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './tour-form.component.html',
  styleUrl: './tour-form.component.scss'
})
export class TourFormComponent implements OnInit {
  @Input() tour: Tour | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  name = '';
  description = '';
  from = '';
  to = '';
  transportType = 'Bike';
  distance: number | null = null;
  estimatedTimeMinutes: number | null = null;
  errorMessage = '';

  transportTypes = ['Bike', 'Hike', 'Running', 'Vacation'];

  constructor(private tourService: TourService) {}

  ngOnInit(): void {
    if (this.tour) {
      this.name = this.tour.name;
      this.description = this.tour.description;
      this.from = this.tour.from;
      this.to = this.tour.to;
      this.transportType = this.tour.transportType;
      this.distance = this.tour.distance;
      this.estimatedTimeMinutes = this.tour.estimatedTimeMinutes;
    }
  }

  onSave(): void {
    if (!this.name || !this.from || !this.to || !this.distance || !this.estimatedTimeMinutes) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }
    if (this.distance <= 0 || this.estimatedTimeMinutes <= 0) {
      this.errorMessage = 'Distance and time must be positive.';
      return;
    }

    const request: TourRequest = {
      name: this.name,
      description: this.description,
      from: this.from,
      to: this.to,
      transportType: this.transportType,
      distance: this.distance,
      estimatedTimeMinutes: this.estimatedTimeMinutes
    };

    const action = this.tour
      ? this.tourService.update(this.tour.id, request)
      : this.tourService.create(request);

    action.subscribe({
      next: () => this.saved.emit(),
      error: () => this.errorMessage = 'Failed to save tour.'
    });
  }
}
