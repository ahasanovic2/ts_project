import jwtDecode from 'jwt-decode';
import { useHistory } from 'react-router-dom';

export const useHandleLogout = () => {
    const history = useHistory();

    return () => {
        const token = localStorage.getItem('access_token');
        const decoded = jwtDecode(token)
        const headers = new Headers();
        headers.append('Content-Type', 'application/json');
        headers.append('Authorization', `Bearer ${token}`);
        const response = fetch(`http://localhost:8080/users/front-logout?email=${decoded.sub}`, {
            method: 'POST',
            headers
        })
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        history.push("/home");
    };
};

export const checkExpiration = (token, logoutFunction) => {
    try {
        const decoded = jwtDecode(token);
        if (decoded) {
            const currentTime = Date.now().valueOf() / 1000;
            if (decoded.exp < currentTime) {
                logoutFunction();
                alert('Your session has expired. You have to login again.');
            }
        }
    } catch (error) {
        console.error('Error decoding token: ', error);
    }
}

export const RefreshToken = async () => {
    const BASE_URL = 'http://localhost:8080';
    const token = localStorage.getItem('refresh_token');
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append('Authorization', `Bearer ${token}`);
    const response = await fetch(`${BASE_URL}/authentication/refresh-token`, {
        method: 'POST',
        headers
    });
    const data = await response.json();
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');

    localStorage.setItem('access_token',data.access_token);
    localStorage.setItem('refresh_token',data.refresh_token);
};
