do
$$
    declare
        x record;
        y record;

    begin
        for x in (select id, row_number() over (order by id) rn from t_user limit 100)
            loop
                for y in (select * from (select id, row_number() over (order by id) rn from t_user limit 100) as ir where ir.rn > x.rn)
                    loop
                        insert into t_friend_relationship(id, since, user_1_id, user_2_id)
                        values (uuid_generate_v4(), now(), x.id, y.id);
                    end loop;
            end loop;
    end;
$$