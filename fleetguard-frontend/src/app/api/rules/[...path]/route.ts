import { NextRequest, NextResponse } from 'next/server';

const RULES_URL = process.env.RULES_SERVICE_URL ?? 'http://localhost:8093';

export async function GET(req: NextRequest, { params }: { params: { path: string[] } }) {
  return proxyRequest(req, params.path, 'GET');
}

export async function POST(req: NextRequest, { params }: { params: { path: string[] } }) {
  return proxyRequest(req, params.path, 'POST');
}

export async function PUT(req: NextRequest, { params }: { params: { path: string[] } }) {
  return proxyRequest(req, params.path, 'PUT');
}

export async function DELETE(req: NextRequest, { params }: { params: { path: string[] } }) {
  return proxyRequest(req, params.path, 'DELETE');
}

async function proxyRequest(req: NextRequest, path: string[], method: string) {
  const targetUrl = `${RULES_URL}/${path.join('/')}`;
  const body = method !== 'GET' ? await req.text() : undefined;

  const response = await fetch(targetUrl, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body,
  });

  const data = await response.text();
  return new NextResponse(data, {
    status: response.status,
    headers: { 'Content-Type': 'application/json' },
  });
}