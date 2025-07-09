import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        constant_rps: {
            executor: 'constant-arrival-rate',
            rate: 500, // 500 requests per second
            timeUnit: '1s',
            duration: '1m', // test duration
            preAllocatedVUs: 200, // adjust as needed
            maxVUs: 1000, // adjust as needed
        },
    },
};

export default function () {
    const url = 'http://localhost:8072/comm-service/api/v1/write/message';
    const payload = JSON.stringify({
        "senderId":"867123245",
        "receiverId":"3463466632",
        "context":"QUOTE",
        "contextId":"685982e0f2e4e61dd8ad8b11",
        "content":"message test one"
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': '323381986'
        },
    };

    const res = http.post(url, payload, params);
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}