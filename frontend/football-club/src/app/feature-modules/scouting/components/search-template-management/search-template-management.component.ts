import { Component, OnInit } from '@angular/core';
import { SearchTemplateService } from '../../services/search-template.service';
import { SearchTemplate, SearchTemplateSave, TemplatePart } from '../../models/search-template.model';

interface UIParamPart extends TemplatePart {
  tempWeight: number;
  markedForDeletion: boolean;
}

interface UISearchTemplate extends SearchTemplate {
  uiParts: UIParamPart[];
  tempName: string;
  isDirty: boolean;
}

@Component({
  selector: 'app-search-template-management',
  templateUrl: './search-template-management.component.html',
  styleUrls: ['./search-template-management.component.css']
})
export class SearchTemplateManagementComponent implements OnInit {
  templates: UISearchTemplate[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  isRenameModalOpen: boolean = false;
  renameTarget: UISearchTemplate | null = null;
  newNameInput: string = '';

  constructor(private templateService: SearchTemplateService) {}

  ngOnInit(): void {
    this.loadTemplates();
  }

  loadTemplates(): void {
    this.isLoading = true;
    this.templateService.getMyTemplates().subscribe({
      next: (data) => {
        this.templates = data.map(template => ({
          ...template,
          tempName: template.templateName,
          isDirty: false,
          uiParts: template.parts.map(part => ({
            ...part,
            tempWeight: part.weight,
            markedForDeletion: false
          }))
        }));
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Sistemska greška prilikom učitavanja šablona.';
        this.isLoading = false;
      }
    });
  }

  checkDirty(template: UISearchTemplate): void {
    const nameChanged = template.templateName !== template.tempName;
    const partsChanged = template.uiParts.some(
      part => part.markedForDeletion || part.tempWeight !== part.weight
    );
    template.isDirty = nameChanged || partsChanged;
  }

  toggleDeletePart(template: UISearchTemplate, part: UIParamPart): void {
    part.markedForDeletion = !part.markedForDeletion;
    this.checkDirty(template);
  }

  cancelChanges(template: UISearchTemplate): void {
    template.tempName = template.templateName;
    template.uiParts.forEach(part => {
      part.tempWeight = part.weight;
      part.markedForDeletion = false;
    });
    template.isDirty = false;
  }

  openRenameModal(template: UISearchTemplate): void {
    this.renameTarget = template;
    this.newNameInput = template.tempName;
    this.isRenameModalOpen = true;
  }

  closeRenameModal(): void {
    this.isRenameModalOpen = false;
    this.renameTarget = null;
    this.newNameInput = '';
  }

  confirmRename(): void {
    if (this.renameTarget && this.newNameInput.trim().length >= 3) {
      this.renameTarget.tempName = this.newNameInput.trim();
      this.checkDirty(this.renameTarget);
      this.closeRenameModal();
    }
  }

  deleteTemplateCard(id: number): void {
    this.templateService.deleteTemplate(id).subscribe({
      next: () => {
        this.templates = this.templates.filter(t => t.id !== id);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Greška prilikom brisanja šablona.';
      }
    });
  }

  saveChanges(template: UISearchTemplate): void {
    const activeParts = template.uiParts.filter(part => !part.markedForDeletion);
    
    const payload: SearchTemplateSave = {
      templateName: template.tempName,
      parts: activeParts.map(part => ({
        metricId: part.metricId,
        weight: part.tempWeight
      }))
    };

    this.templateService.updateTemplate(template.id, payload).subscribe({
      next: () => {
        this.templateService.getTemplateById(template.id).subscribe({
          next: (updated) => {
            const index = this.templates.findIndex(t => t.id === template.id);
            if (index !== -1) {
              this.templates[index] = {
                ...updated,
                tempName: updated.templateName,
                isDirty: false,
                uiParts: updated.parts.map(part => ({
                  ...part,
                  tempWeight: part.weight,
                  markedForDeletion: false
                }))
              };
            }
          },
          error: (err) => {
            console.error(err);
          }
        });
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Greška prilikom ažuriranja šablona.';
      }
    });
  }
}