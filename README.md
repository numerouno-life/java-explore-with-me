#  Ссылка на PR 
https://github.com/numerouno-life/java-explore-with-me/pull/3

# Функциональность "Комментарии"

1. Добавлена возможность оставлять комментарии к событиям
2. Реализованы ответы на комментарии (вложенность)
3. Внедрено управление статусами комментариев (на проверке, опубликовано, удалено, отклонено)

## Особенности:
1. Комментировать могут только авторизованные пользователи
2. Комментировать можно только опубликованные события
3. Поддерживается мягкое и жесткое удаление комментариев
4. Администратор может управлять статусами комментариев

### Public:
1. Получение комментариев к событию: GET /comments/{eventId}

### Private:
1. Добавление нового комментария: POST /users/{userId}/events/{eventId}
2. Редактирование комментария: PATCH /users/{userId}/comments/{commentId}
3. Удаление комментария: DELETE /users/{userId}/comments/{commentId}
4. Добавление ответа на комментарий: POST /events/{eventId}/comments/{parentCommentId}/replies

### Admin:
1. Поиск комментариев по фильтру: GET /admin/comments/filter
2. Изменение статуса комментария: PATCH /admin/comments/{commentId}
3. Мягкое удаление комментария: DELETE /admin/comments/{commentId}
4. Жесткое удаление комментария: DELETE /admin/comments/{commentId}/hard