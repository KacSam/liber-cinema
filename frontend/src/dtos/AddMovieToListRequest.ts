import { Movie } from '../types/movie';
import { UserListType } from '../types/userListType';

export interface AddMovieToListRequest {
    movie: Movie;
    listType: UserListType;
}
