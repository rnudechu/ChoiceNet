/**
 * Different Message Packet Type Code 
 * @author Robinson Udechukwu
 *
 */
public enum PacketType 
{
	
	RENDEZVOUS_REQUEST,
	RENDEZVOUS_RESPONSE,
	TRANSFER_CONSIDERATION,
	LISTING_REQUEST,
	NACK,
	ACK_AND_SEND_TOKEN,
	LISTING_CONFIRMATION,
	USE_PLANE_SIGNAL,
	USE_ATTEMPT,
	MARKETPLACE_QUERY,
	MARKETPLACE_RESPONSE,
	PLANNER_REQUEST,
	PLANNER_RESPONSE,
	
	ACK_USE_ATTEMPT,
	NACK_USE_ATTEMPT,
	USE_ATTEMPT_STATUS,
	RELEASE_USE_ATTEMPT_RESOURCES,
	ACK_RELEASE_USE_ATTEMPT_RESOURCES,
	NACK_RELEASE_USE_ATTEMPT_RESOURCES, 
}