function response = getResponse(s)
    warning off all
    response = char(fread(s, 1));
    while((isempty(response))||(~strcmp(response(end), char(get(s,'Terminator')))))
        response = [response, char,(fread(s,1))];
    end
    warning on all
end