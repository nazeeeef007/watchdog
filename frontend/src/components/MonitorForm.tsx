// src/components/MonitorForm.tsx

import React from "react";
import type { UseFormReturn } from "react-hook-form";
import type { MonitorFormValues } from "@/forms/monitorFormSchema";
import { Form, FormField, FormItem, FormLabel, FormControl, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";

interface MonitorFormProps {
  form: UseFormReturn<MonitorFormValues>;
  isMutating: boolean;
  isEditMode: boolean;
  mutationError?: string | null;
  onCancel: () => void;
  onSubmit: (values: MonitorFormValues) => void;
}

export const MonitorForm: React.FC<MonitorFormProps> = ({
  form,
  isMutating,
  isEditMode,
  mutationError,
  onCancel,
  onSubmit,
}) => {
  const alertType = form.watch("alertType");

  const getDestinationPlaceholder = () =>
    alertType === "EMAIL" ? "user@example.com" : "https://webhook.example.com";

  const getDestinationLabel = () =>
    alertType === "EMAIL" ? "Email Address" : "Webhook URL";

  return (
    // Corrected: The <Form> component from shadcn/ui provides the context,
    // but the actual form submission logic should be tied to a standard <form> element.
    // The correct way is to wrap the form content in <Form> and then use
    // the handleSubmit method on the <form> element inside it.
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        {mutationError && (
          <Alert variant="destructive">
            <AlertDescription>{mutationError}</AlertDescription>
          </Alert>
        )}
        
        {/* All your FormFields go here */}
        <FormField
          control={form.control}
          name="url"
          render={({ field }) => (
            <FormItem>
              <FormLabel>URL to Monitor</FormLabel>
              <FormControl>
                <Input type="url" placeholder="https://example.com" {...field} disabled={isMutating} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <FormField
          control={form.control}
          name="checkIntervalSeconds"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Check Interval (seconds)</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  placeholder="60"
                  {...field}
                  onChange={(e) => field.onChange(Number(e.target.value) || 0)}
                  min={10}
                  disabled={isMutating}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <FormField
          control={form.control}
          name="type"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Monitor Type</FormLabel>
              <FormControl>
                <select {...field} disabled={isMutating} className="form-select">
                  <option value="HTTP_HTTPS">HTTP/HTTPS</option>
                  <option value="PING">Ping</option>
                  <option value="PORT">Port</option>
                </select>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <FormField
          control={form.control}
          name="alertType"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Alert Type</FormLabel>
              <FormControl>
                <select {...field} disabled={isMutating} className="form-select">
                  <option value="EMAIL">Email</option>
                  <option value="WEBHOOK">Webhook</option>
                </select>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <FormField
          control={form.control}
          name="destination"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{getDestinationLabel()}</FormLabel>
              <FormControl>
                <Input
                  {...field}
                  placeholder={getDestinationPlaceholder()}
                  disabled={isMutating}
                  type={alertType === "EMAIL" ? "email" : "url"}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex gap-3 pt-4 justify-end">
          <Button type="button" variant="outline" onClick={onCancel} disabled={isMutating}>
            Cancel
          </Button>
          <Button type="submit" disabled={isMutating}>
            {isMutating
              ? "Saving..."
              : isEditMode
              ? "Update Monitor"
              : "Create Monitor"}
          </Button>
        </div>
      </form>
    </Form>
  );
};