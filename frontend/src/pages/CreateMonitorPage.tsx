import React, { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "@/context/AuthContext";
import { MainLayout } from "@/components/MainLayout";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { useMonitors } from "@/hooks/useMonitors";
import { monitorFormSchema, defaultFormValues, monitorToFormValues, formValuesToRequest } from "@/forms/monitorFormSchema";
import { MonitorForm } from "@/components/MonitorForm";
import type { MonitorFormValues } from "@/forms/monitorFormSchema";
export const CreateMonitorPage: React.FC = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const { monitorId } = useParams<{ monitorId: string }>();
  const parsedMonitorId = monitorId ? parseInt(monitorId, 10) : undefined;
  const isEditMode = Boolean(parsedMonitorId);

  const { createMonitor, updateMonitor, fetchMonitorById, isMutating, mutationError, isLoading } = useMonitors();

  const form = useForm<MonitorFormValues>({
    resolver: zodResolver(monitorFormSchema),
    defaultValues: defaultFormValues,
  });

  const { reset } = form;

  useEffect(() => {
    if (!isEditMode || !parsedMonitorId) return;
    fetchMonitorById(parsedMonitorId)
      .then(monitor => monitor && reset(monitorToFormValues(monitor)))
      .catch(() => navigate("/dashboard"));
  }, [isEditMode, parsedMonitorId, fetchMonitorById, reset, navigate]);

  const onSubmit = async (values: MonitorFormValues) => {
    const data = formValuesToRequest(values);
    if (isEditMode && parsedMonitorId) {
      await updateMonitor(parsedMonitorId, data);
    } else {
      await createMonitor(data);
    }
    navigate("/dashboard");
  };

  if (isEditMode && isLoading) {
    return (
      <MainLayout onLogout={logout}>
        <Card className="max-w-xl p-8 mx-auto">
          <CardContent>
            <div className="flex items-center justify-center py-8 text-muted-foreground">
              Loading monitor data...
            </div>
          </CardContent>
        </Card>
      </MainLayout>
    );
  }

  return (
    <MainLayout onLogout={logout}>
      <Card className="max-w-xl p-8 mx-auto space-y-6">
        <CardHeader className="text-center">
          <CardTitle>{isEditMode ? "Edit Monitor" : "Create New Monitor"}</CardTitle>
          <CardDescription>
            {isEditMode
              ? "Update your existing monitor configuration."
              : "Configure a new service to monitor for uptime and performance."}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <MonitorForm
            form={form}
            isMutating={isMutating}
            isEditMode={isEditMode}
            mutationError={mutationError}
            onCancel={() => navigate("/dashboard")}
            onSubmit={onSubmit}
          />
        </CardContent>
      </Card>
    </MainLayout>
  );
};
