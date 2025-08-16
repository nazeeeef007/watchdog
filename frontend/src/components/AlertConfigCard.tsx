import React, { useState } from "react";
import type { AlertConfigDTO, AlertType } from "@/types/AlertConfigDTO";
import type { CreateAlertConfigRequest } from "@/types/CreateAlertConfigRequest";
import { X, Check, Edit, Trash2, Mail, Bell, MessageSquare, Globe } from "lucide-react";
import { Card } from "./ui/card";
import { Button } from "./ui/button";
import { Badge } from "./ui/badge"; // Using a shadcn/ui Badge for status
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select";
import { Switch } from "./ui/switch"; // Using a modern switch component

// Props for the AlertConfigCard component
interface AlertConfigCardProps {
  config: AlertConfigDTO;
  onUpdate: (configId: number, updatedConfig: CreateAlertConfigRequest) => Promise<void>;
  onDelete: (configId: number) => Promise<void>;
  isLoading: boolean;
}

const AlertConfigCard: React.FC<AlertConfigCardProps> = ({ config, onUpdate, onDelete, isLoading }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState<CreateAlertConfigRequest>({
    type: config.type,
    destination: config.destination,
    enabled: config.enabled,
    failureThreshold: config.failureThreshold,
    recoveryThreshold: config.recoveryThreshold
  });

  const getIconForType = (type: AlertType) => {
    switch (type) {
      case "EMAIL":
        return <Mail size={20} className="text-blue-500" />;
      case "DISCORD_WEBHOOK":
        return <MessageSquare size={20} className="text-indigo-500" />;
      case "TELEGRAM":
        return <Bell size={20} className="text-sky-500" />;
      case "GENERIC_WEBHOOK":
      default:
        return <Globe size={20} className="text-gray-500" />;
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSelectChange = (value: string) => {
    setFormData((prevData) => ({
      ...prevData,
      type: value as AlertType,
    }));
  };

  const handleUpdate = async () => {
    // NOTE: This should be replaced with a custom modal, not window.confirm
    // as per best practices and to avoid blocking the UI.
    const confirmed = true; // Placeholder for custom modal
    if (confirmed) {
      try {
        await onUpdate(config.id, formData);
        setIsEditing(false); // Exit edit mode on success
      } catch (error) {
        console.error("Update failed:", error);
      }
    }
  };

  const handleDelete = async () => {
    // NOTE: This should be replaced with a custom modal.
    const confirmed = true; // Placeholder for custom modal
    if (confirmed) {
      try {
        await onDelete(config.id);
      } catch (error) {
        console.error("Deletion failed:", error);
      }
    }
  };

  return (
    <Card className="p-6 transition-all duration-300 ease-in-out hover:shadow-lg">
      {isEditing ? (
        // Edit Mode
        <div className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-1">
              <Label htmlFor="type">Alert Type</Label>
              <Select name="type" value={formData.type} onValueChange={handleSelectChange} disabled={isLoading}>
                <SelectTrigger>
                  <SelectValue placeholder="Select an alert type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="EMAIL">Email</SelectItem>
                  <SelectItem value="DISCORD_WEBHOOK">Discord Webhook</SelectItem>
                  <SelectItem value="TELEGRAM">Telegram</SelectItem>
                  <SelectItem value="GENERIC_WEBHOOK">Generic Webhook</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1">
              <Label htmlFor="destination">Destination</Label>
              <Input
                type="text"
                id="destination"
                name="destination"
                value={formData.destination}
                onChange={handleInputChange}
                disabled={isLoading}
              />
            </div>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <Switch
                id="enabled"
                name="enabled"
                checked={formData.enabled}
                onCheckedChange={(checked) => setFormData(prev => ({ ...prev, enabled: checked }))}
                disabled={isLoading}
              />
              <Label htmlFor="enabled">Enabled</Label>
            </div>
            <div className="flex space-x-2">
              <Button
                variant="ghost"
                onClick={() => setIsEditing(false)}
                disabled={isLoading}
              >
                <X size={20} className="mr-2" />
                Cancel
              </Button>
              <Button
                onClick={handleUpdate}
                disabled={isLoading}
              >
                <Check size={20} className="mr-2" />
                Save
              </Button>
            </div>
          </div>
        </div>
      ) : (
        // View Mode
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <div className="flex-shrink-0">{getIconForType(config.type)}</div>
            <div>
              <h4 className="text-lg font-semibold">{config.type}</h4>
              <p className="text-sm text-gray-500 truncate max-w-[200px] md:max-w-none">
                {config.destination}
              </p>
            </div>
            <Badge
              variant={config.enabled ? "default" : "destructive"}
              className="ml-auto"
            >
              {config.enabled ? "Enabled" : "Disabled"}
            </Badge>
          </div>
          <div className="flex-shrink-0 flex items-center space-x-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setIsEditing(true)}
              disabled={isLoading}
            >
              <Edit size={16} />
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={handleDelete}
              disabled={isLoading}
              className="text-red-500 hover:bg-red-50"
            >
              <Trash2 size={16} />
            </Button>
          </div>
        </div>
      )}
    </Card>
  );
};

export default AlertConfigCard;
