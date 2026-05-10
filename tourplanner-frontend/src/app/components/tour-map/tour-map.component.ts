import { Component, Input, OnChanges, OnDestroy, SimpleChanges, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';

// Fix for missing marker icons in Angular
const iconDefault = L.icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
L.Marker.prototype.options.icon = iconDefault;

@Component({
  selector: 'app-tour-map',
  standalone: true,
  imports: [CommonModule],
  template: `<div id="tour-map-{{ mapId }}" style="height: 300px; width: 100%; border-radius: 8px;"></div>`,
  styles: [`:host { display: block; }`]
})
export class TourMapComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() fromLocation: string = '';
  @Input() toLocation: string = '';
  @Input() tourName: string = '';

  mapId = Math.random().toString(36).substring(2, 9);
  private map: L.Map | null = null;

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.map && (changes['fromLocation'] || changes['toLocation'])) {
      this.updateMap();
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  private initMap(): void {
    setTimeout(() => {
      const container = document.getElementById(`tour-map-${this.mapId}`);
      if (!container) return;

      this.map = L.map(`tour-map-${this.mapId}`).setView([48.2082, 16.3738], 7);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
      }).addTo(this.map);

      this.updateMap();
    }, 100);
  }

  private updateMap(): void {
    if (!this.map) return;

    this.map.eachLayer(layer => {
      if (layer instanceof L.Marker) {
        this.map!.removeLayer(layer);
      }
    });

    if (this.fromLocation) {
      this.geocodeAndMark(this.fromLocation, `Start: ${this.fromLocation}`);
    }
    if (this.toLocation) {
      this.geocodeAndMark(this.toLocation, `End: ${this.toLocation}`);
    }
  }

  private geocodeAndMark(location: string, label: string): void {
    const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(location)}`;
    fetch(url)
      .then(res => res.json())
      .then(data => {
        if (data && data.length > 0) {
          const lat = parseFloat(data[0].lat);
          const lon = parseFloat(data[0].lon);
          L.marker([lat, lon]).addTo(this.map!).bindPopup(label);
          this.map!.setView([lat, lon], 10);
        }
      })
      .catch(err => console.error('Geocoding error:', err));
  }
}
