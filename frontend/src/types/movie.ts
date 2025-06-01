export interface Movie {
    id?: number;
    title: string;
    description?: string;
    director?: string;
    genre?: string;
    releaseDate?: string;
    duration?: string;
    imdbRating?: number;
    userRating?: number;
    isFavorite?: boolean;
}
