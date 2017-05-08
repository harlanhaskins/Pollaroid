select
  poll.*,
  count(poll_record.*) as vote_count,
  (select option
   from poll_option
   where poll_option.poll_id = poll.id
   order by votes desc limit 1) as popular_option
from poll left join poll_record on poll.id = poll_record.poll_id
group by poll.id
order by vote_count desc
limit ?;
