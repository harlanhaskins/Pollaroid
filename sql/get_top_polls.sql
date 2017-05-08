select
  poll.*,
  count(poll_record.*) as vote_count,
  (select option
   from poll_option
   where poll_option.poll_id = poll.id
   order by votes desc limit 1) as popular_option
from (
	select *
	from (poll left join poll_record on poll.id = poll_record.poll_id)
	where poll.district_id in ((select house_district_id from voter where voter.id = ?) union (select senate_district_id from voter where voter.id = ?))
)
group by poll.id
order by vote_count desc
limit ?;
