import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScoutingRequestsComponent } from './scouting-requests.component';

describe('ScoutingRequestsComponent', () => {
  let component: ScoutingRequestsComponent;
  let fixture: ComponentFixture<ScoutingRequestsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ScoutingRequestsComponent]
    });
    fixture = TestBed.createComponent(ScoutingRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
