select poll.*, count(poll_record.*) as vote_count
from poll left join poll_record on poll.id = poll_record.poll_id
group by poll.id
order by vote_count desc
limit ?;