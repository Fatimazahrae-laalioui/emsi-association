package ma.PFA.repository;

import ma.PFA.entity.ForumReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumReplyRepository extends JpaRepository<ForumReply, Long> {
}