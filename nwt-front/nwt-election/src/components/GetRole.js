import jwtDecode from 'jwt-decode';

const GetRole = (token) => {
    const decodedToken = jwtDecode(token);
    return decodedToken.role;
};

export default GetRole;