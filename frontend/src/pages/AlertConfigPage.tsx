import React, { useState } from "react";
import { useAlertConfig } from "@/hooks/useAlertConfig";
import AlertConfigCard from "../components/AlertConfigCard";
import type { CreateAlertConfigRequest } from "@/types/CreateAlertConfigRequest";
import { Plus } from "lucide-react";
import { useParams } from "react-router";
import { MainLayout } from "@/components/MainLayout";
import { useAuth } from "@/context/AuthContext";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";

const AlertConfigPage = () => {
  const { logout } = useAuth();
  const { monitorId } = useParams<{ monitorId: string }>();
  const parsedMonitorId = monitorId ? parseInt(monitorId, 10) : 0;
  const { alertConfigs, isLoading, error, createAlertConfig, updateAlertConfig, deleteAlertConfig } = useAlertConfig(parsedMonitorId);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newConfigData, setNewConfigData] = useState<CreateAlertConfigRequest>({
    type: "EMAIL",
    destination: "",
    enabled: true,
    failureThreshold: 3, // Now includes a default value
    recoveryThreshold: 3, // Now includes a default value
  });

  const handleNewConfigChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type } = e.target;
    setNewConfigData((prevData) => ({
      ...prevData,
      [name]: type === 'number' ? Number(value) : value,
    }));
  };

  const handleNewConfigSelectChange = (value: string) => {
    setNewConfigData((prevData) => ({
      ...prevData,
      type: value as CreateAlertConfigRequest['type'],
    }));
  };

  const handleCreateNewAlert = async () => {
    try {
      await createAlertConfig(newConfigData);
      setIsModalOpen(false);
      setNewConfigData({ type: "EMAIL", destination: "", enabled: true, failureThreshold: 3, recoveryThreshold: 3 });
    } catch (err) {
      console.error("Failed to create new alert:", err);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <div className="text-xl font-medium text-gray-500">Loading alert configurations...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <div className="text-xl font-medium text-red-500">Error: {error}</div>
      </div>
    );
  }

  return (
    <MainLayout onLogout={logout}>
      <div className="min-h-screen bg-gray-50 p-8">
        <div className="max-w-4xl mx-auto">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-3xl font-bold text-gray-900">Alert Configurations</h1>
            <Button
              onClick={() => setIsModalOpen(true)}
              className="flex items-center"
            >
              <Plus size={20} className="mr-2" />
              Add New Alert
            </Button>
          </div>

          {alertConfigs && alertConfigs.length > 0 ? (
            <div className="space-y-4">
              {alertConfigs.map((config) => (
                <AlertConfigCard
                  key={config.id}
                  config={config}
                  onUpdate={updateAlertConfig}
                  onDelete={deleteAlertConfig}
                  isLoading={isLoading}
                />
              ))}
            </div>
          ) : (
            <div className="text-center p-10 bg-white rounded-xl shadow-md border border-gray-200 text-gray-500">
              No alert configurations found for this monitor.
            </div>
          )}
        </div>

        <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Create New Alert</DialogTitle>
              <DialogDescription>
                Add a new alert configuration for this monitor.
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div className="space-y-1">
                <Label htmlFor="type">Alert Type</Label>
                <Select
                  name="type"
                  value={newConfigData.type}
                  onValueChange={handleNewConfigSelectChange}
                >
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
                  id="destination"
                  type="text"
                  name="destination"
                  value={newConfigData.destination}
                  onChange={handleNewConfigChange}
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <Label htmlFor="failureThreshold">Failure Threshold</Label>
                  <Input
                    id="failureThreshold"
                    type="number"
                    name="failureThreshold"
                    value={newConfigData.failureThreshold}
                    onChange={handleNewConfigChange}
                  />
                </div>
                <div className="space-y-1">
                  <Label htmlFor="recoveryThreshold">Recovery Threshold</Label>
                  <Input
                    id="recoveryThreshold"
                    type="number"
                    name="recoveryThreshold"
                    value={newConfigData.recoveryThreshold}
                    onChange={handleNewConfigChange}
                  />
                </div>
              </div>
              <div className="flex items-center space-x-2">
                <Switch
                  id="enabled"
                  name="enabled"
                  checked={newConfigData.enabled}
                  onCheckedChange={(checked) => setNewConfigData(prev => ({ ...prev, enabled: checked }))}
                />
                <Label htmlFor="enabled">Enabled</Label>
              </div>
            </div>
            <DialogFooter>
              <Button onClick={() => setIsModalOpen(false)} variant="outline">
                Cancel
              </Button>
              <Button onClick={handleCreateNewAlert} disabled={isLoading}>
                Create Alert
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </MainLayout>
  );
};

export default AlertConfigPage;
