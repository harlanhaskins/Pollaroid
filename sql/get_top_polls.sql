select poll_id, count(*)
from poll_record
group by poll_id
order by count(*) desc
limit ?;