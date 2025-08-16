import * as z from "zod";
import type { MonitorDTO } from "@/types/MonitorDTO";
import type { CreateMonitorRequest } from "@/types/CreateMonitorRequest";

export const monitorFormSchema = z
  .object({
    url: z.string().url({ message: "Invalid URL format." }),
    checkIntervalSeconds: z.preprocess(
      (val) => (typeof val === "string" ? Number(val) : val),
      z.number().min(10, { message: "Interval must be at least 10 seconds." })
    ),
    type: z.enum(["HTTP_HTTPS", "PING", "PORT"]),
    alertType: z.enum(["EMAIL", "WEBHOOK"]),
    destination: z.string().min(1, { message: "Destination is required." }),
  })
  .refine((data) => {
    if (data.alertType === "EMAIL") {
      return z.string().email().safeParse(data.destination).success;
    }
    if (data.alertType === "WEBHOOK") {
      return z.string().url().safeParse(data.destination).success;
    }
    return true;
  }, {
    message: "Invalid destination format for selected alert type.",
    path: ["destination"],
  });


export type MonitorFormValues = z.infer<typeof monitorFormSchema>;

export const defaultFormValues: MonitorFormValues = {
  url: "",
  checkIntervalSeconds: 60,
  type: "HTTP_HTTPS",
  alertType: "EMAIL",
  destination: "",
};

export const monitorToFormValues = (monitor: MonitorDTO): MonitorFormValues => ({
  url: monitor.url,
  checkIntervalSeconds: monitor.checkIntervalSeconds,
  type: monitor.type,
  alertType: "EMAIL",
  destination: "youremail@gmail.com"
});

export const formValuesToRequest = (values: MonitorFormValues): CreateMonitorRequest => ({
  url: values.url,
  checkIntervalSeconds: values.checkIntervalSeconds,
  type: values.type,
  alertConfig: {
    alertType: values.alertType,
    destination: values.destination,
  },
});
