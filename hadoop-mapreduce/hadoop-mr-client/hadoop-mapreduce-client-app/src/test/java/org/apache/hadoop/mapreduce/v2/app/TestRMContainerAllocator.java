begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptContainerAssignedEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|ContainerRequestEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|RMContainerAllocator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
operator|.
name|NetworkTopology
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|Clock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|YarnException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|AMRMProtocol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|FinishApplicationMasterResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterApplicationMasterResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|AMResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationMaster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Container
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ResourceRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|event
operator|.
name|Event
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|event
operator|.
name|EventHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRemoteException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|factories
operator|.
name|RecordFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|ipc
operator|.
name|RPCUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|ResourceTrackerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|StoreFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNodeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|Allocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|ResourceScheduler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fifo
operator|.
name|FifoScheduler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|security
operator|.
name|ContainerTokenSecretManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestRMContainerAllocator
specifier|public
class|class
name|TestRMContainerAllocator
block|{
comment|//  private static final Log LOG = LogFactory.getLog(TestRMContainerAllocator.class);
comment|//  private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
comment|//
comment|//  @BeforeClass
comment|//  public static void preTests() {
comment|//    DefaultMetricsSystem.shutdown();
comment|//  }
comment|//
comment|//  @Test
comment|//  public void testSimple() throws Exception {
comment|//    FifoScheduler scheduler = createScheduler();
comment|//    LocalRMContainerAllocator allocator = new LocalRMContainerAllocator(
comment|//        scheduler, new Configuration());
comment|//
comment|//    //add resources to scheduler
comment|//    RMNode nodeManager1 = addNode(scheduler, "h1", 10240);
comment|//    RMNode nodeManager2 = addNode(scheduler, "h2", 10240);
comment|//    RMNode nodeManager3 = addNode(scheduler, "h3", 10240);
comment|//
comment|//    //create the container request
comment|//    ContainerRequestEvent event1 =
comment|//      createReq(1, 1024, new String[]{"h1"});
comment|//    allocator.sendRequest(event1);
comment|//
comment|//    //send 1 more request with different resource req
comment|//    ContainerRequestEvent event2 = createReq(2, 1024, new String[]{"h2"});
comment|//    allocator.sendRequest(event2);
comment|//
comment|//    //this tells the scheduler about the requests
comment|//    //as nodes are not added, no allocations
comment|//    List<TaskAttemptContainerAssignedEvent> assigned = allocator.schedule();
comment|//    Assert.assertEquals("No of assignments must be 0", 0, assigned.size());
comment|//
comment|//    //send another request with different resource and priority
comment|//    ContainerRequestEvent event3 = createReq(3, 1024, new String[]{"h3"});
comment|//    allocator.sendRequest(event3);
comment|//
comment|//    //this tells the scheduler about the requests
comment|//    //as nodes are not added, no allocations
comment|//    assigned = allocator.schedule();
comment|//    Assert.assertEquals("No of assignments must be 0", 0, assigned.size());
comment|//
comment|//    //update resources in scheduler
comment|//    scheduler.nodeUpdate(nodeManager1); // Node heartbeat
comment|//    scheduler.nodeUpdate(nodeManager2); // Node heartbeat
comment|//    scheduler.nodeUpdate(nodeManager3); // Node heartbeat
comment|//
comment|//
comment|//    assigned = allocator.schedule();
comment|//    checkAssignments(
comment|//        new ContainerRequestEvent[]{event1, event2, event3}, assigned, false);
comment|//  }
comment|//
comment|//  //TODO: Currently Scheduler seems to have bug where it does not work
comment|//  //for Application asking for containers with different capabilities.
comment|//  //@Test
comment|//  public void testResource() throws Exception {
comment|//    FifoScheduler scheduler = createScheduler();
comment|//    LocalRMContainerAllocator allocator = new LocalRMContainerAllocator(
comment|//        scheduler, new Configuration());
comment|//
comment|//    //add resources to scheduler
comment|//    RMNode nodeManager1 = addNode(scheduler, "h1", 10240);
comment|//    RMNode nodeManager2 = addNode(scheduler, "h2", 10240);
comment|//    RMNode nodeManager3 = addNode(scheduler, "h3", 10240);
comment|//
comment|//    //create the container request
comment|//    ContainerRequestEvent event1 =
comment|//      createReq(1, 1024, new String[]{"h1"});
comment|//    allocator.sendRequest(event1);
comment|//
comment|//    //send 1 more request with different resource req
comment|//    ContainerRequestEvent event2 = createReq(2, 2048, new String[]{"h2"});
comment|//    allocator.sendRequest(event2);
comment|//
comment|//    //this tells the scheduler about the requests
comment|//    //as nodes are not added, no allocations
comment|//    List<TaskAttemptContainerAssignedEvent> assigned = allocator.schedule();
comment|//    Assert.assertEquals("No of assignments must be 0", 0, assigned.size());
comment|//
comment|//    //update resources in scheduler
comment|//    scheduler.nodeUpdate(nodeManager1); // Node heartbeat
comment|//    scheduler.nodeUpdate(nodeManager2); // Node heartbeat
comment|//    scheduler.nodeUpdate(nodeManager3); // Node heartbeat
comment|//
comment|//    assigned = allocator.schedule();
comment|//    checkAssignments(
comment|//        new ContainerRequestEvent[]{event1, event2}, assigned, false);
comment|//  }
comment|//
comment|//  @Test
comment|//  public void testMapReduceScheduling() throws Exception {
comment|//    FifoScheduler scheduler = createScheduler();
comment|//    Configuration conf = new Configuration();
comment|//    LocalRMContainerAllocator allocator = new LocalRMContainerAllocator(
comment|//        scheduler, conf);
comment|//
comment|//    //add resources to scheduler
comment|//    RMNode nodeManager1 = addNode(scheduler, "h1", 1024);
comment|//    RMNode nodeManager2 = addNode(scheduler, "h2", 10240);
comment|//    RMNode nodeManager3 = addNode(scheduler, "h3", 10240);
comment|//
comment|//    //create the container request
comment|//    //send MAP request
comment|//    ContainerRequestEvent event1 =
comment|//      createReq(1, 2048, new String[]{"h1", "h2"}, true, false);
comment|//    allocator.sendRequest(event1);
comment|//
comment|//    //send REDUCE request
comment|//    ContainerRequestEvent event2 = createReq(2, 3000, new String[]{"h1"}, false, true);
comment|//    allocator.sendRequest(event2);
comment|//
comment|//    //send MAP request
comment|//    ContainerRequestEvent event3 = createReq(3, 2048, new String[]{"h3"}, false, false);
comment|//    allocator.sendRequest(event3);
comment|//
comment|//    //this tells the scheduler about the requests
comment|//    //as nodes are not added, no allocations
comment|//    List<TaskAttemptContainerAssignedEvent> assigned = allocator.schedule();
comment|//    Assert.assertEquals("No of assignments must be 0", 0, assigned.size());
comment|//
comment|//    //update resources in scheduler
comment|//    scheduler.nodeUpdate(nodeManager1); // Node heartbeat
comment|//    scheduler.nodeUpdate(nodeManager2); // Node heartbeat
comment|//    scheduler.nodeUpdate(nodeManager3); // Node heartbeat
comment|//
comment|//    assigned = allocator.schedule();
comment|//    checkAssignments(
comment|//        new ContainerRequestEvent[]{event1, event3}, assigned, false);
comment|//
comment|//    //validate that no container is assigned to h1 as it doesn't have 2048
comment|//    for (TaskAttemptContainerAssignedEvent assig : assigned) {
comment|//      Assert.assertFalse("Assigned count not correct",
comment|//          "h1".equals(assig.getContainer().getNodeId().getHost()));
comment|//    }
comment|//  }
comment|//
comment|//
comment|//
comment|//  private RMNode addNode(FifoScheduler scheduler,
comment|//      String nodeName, int memory) {
comment|//    NodeId nodeId = recordFactory.newRecordInstance(NodeId.class);
comment|//    nodeId.setHost(nodeName);
comment|//    nodeId.setPort(1234);
comment|//    Resource resource = recordFactory.newRecordInstance(Resource.class);
comment|//    resource.setMemory(memory);
comment|//    RMNode nodeManager = new RMNodeImpl(nodeId, null, nodeName, 0, 0,
comment|//        ResourceTrackerService.resolve(nodeName), resource);
comment|//    scheduler.addNode(nodeManager); // Node registration
comment|//    return nodeManager;
comment|//  }
comment|//
comment|//  private FifoScheduler createScheduler() throws YarnRemoteException {
comment|//    FifoScheduler fsc = new FifoScheduler() {
comment|//      //override this to copy the objects
comment|//      //otherwise FifoScheduler updates the numContainers in same objects as kept by
comment|//      //RMContainerAllocator
comment|//
comment|//      @Override
comment|//      public synchronized void allocate(ApplicationAttemptId applicationId,
comment|//          List<ResourceRequest> ask) {
comment|//        List<ResourceRequest> askCopy = new ArrayList<ResourceRequest>();
comment|//        for (ResourceRequest req : ask) {
comment|//          ResourceRequest reqCopy = recordFactory.newRecordInstance(ResourceRequest.class);
comment|//          reqCopy.setPriority(req.getPriority());
comment|//          reqCopy.setHostName(req.getHostName());
comment|//          reqCopy.setCapability(req.getCapability());
comment|//          reqCopy.setNumContainers(req.getNumContainers());
comment|//          askCopy.add(reqCopy);
comment|//        }
comment|//        super.allocate(applicationId, askCopy);
comment|//      }
comment|//    };
comment|//    try {
comment|//      fsc.reinitialize(new Configuration(), new ContainerTokenSecretManager(), null);
comment|//      fsc.addApplication(recordFactory.newRecordInstance(ApplicationId.class),
comment|//          recordFactory.newRecordInstance(ApplicationMaster.class),
comment|//          "test", null, null, StoreFactory.createVoidAppStore());
comment|//    } catch(IOException ie) {
comment|//      LOG.info("add application failed with ", ie);
comment|//      assert(false);
comment|//    }
comment|//    return fsc;
comment|//  }
comment|//
comment|//  private ContainerRequestEvent createReq(
comment|//      int attemptid, int memory, String[] hosts) {
comment|//    return createReq(attemptid, memory, hosts, false, false);
comment|//  }
comment|//
comment|//  private ContainerRequestEvent createReq(
comment|//      int attemptid, int memory, String[] hosts, boolean earlierFailedAttempt, boolean reduce) {
comment|//    ApplicationId appId = recordFactory.newRecordInstance(ApplicationId.class);
comment|//    appId.setClusterTimestamp(0);
comment|//    appId.setId(0);
comment|//    JobId jobId = recordFactory.newRecordInstance(JobId.class);
comment|//    jobId.setAppId(appId);
comment|//    jobId.setId(0);
comment|//    TaskId taskId = recordFactory.newRecordInstance(TaskId.class);
comment|//    taskId.setId(0);
comment|//    taskId.setJobId(jobId);
comment|//    if (reduce) {
comment|//      taskId.setTaskType(TaskType.REDUCE);
comment|//    } else {
comment|//      taskId.setTaskType(TaskType.MAP);
comment|//    }
comment|//    TaskAttemptId attemptId = recordFactory.newRecordInstance(TaskAttemptId.class);
comment|//    attemptId.setId(attemptid);
comment|//    attemptId.setTaskId(taskId);
comment|//    Resource containerNeed = recordFactory.newRecordInstance(Resource.class);
comment|//    containerNeed.setMemory(memory);
comment|//    if (earlierFailedAttempt) {
comment|//      return ContainerRequestEvent.
comment|//           createContainerRequestEventForFailedContainer(attemptId, containerNeed);
comment|//    }
comment|//    return new ContainerRequestEvent(attemptId,
comment|//        containerNeed,
comment|//        hosts, new String[] {NetworkTopology.DEFAULT_RACK});
comment|//  }
comment|//
comment|//  private void checkAssignments(ContainerRequestEvent[] requests,
comment|//      List<TaskAttemptContainerAssignedEvent> assignments,
comment|//      boolean checkHostMatch) {
comment|//    Assert.assertNotNull("Container not assigned", assignments);
comment|//    Assert.assertEquals("Assigned count not correct",
comment|//        requests.length, assignments.size());
comment|//
comment|//    //check for uniqueness of containerIDs
comment|//    Set<ContainerId> containerIds = new HashSet<ContainerId>();
comment|//    for (TaskAttemptContainerAssignedEvent assigned : assignments) {
comment|//      containerIds.add(assigned.getContainer().getId());
comment|//    }
comment|//    Assert.assertEquals("Assigned containers must be different",
comment|//        assignments.size(), containerIds.size());
comment|//
comment|//    //check for all assignment
comment|//    for (ContainerRequestEvent req : requests) {
comment|//      TaskAttemptContainerAssignedEvent assigned = null;
comment|//      for (TaskAttemptContainerAssignedEvent ass : assignments) {
comment|//        if (ass.getTaskAttemptID().equals(req.getAttemptID())){
comment|//          assigned = ass;
comment|//          break;
comment|//        }
comment|//      }
comment|//      checkAssignment(req, assigned, checkHostMatch);
comment|//    }
comment|//  }
comment|//
comment|//  private void checkAssignment(ContainerRequestEvent request,
comment|//      TaskAttemptContainerAssignedEvent assigned, boolean checkHostMatch) {
comment|//    Assert.assertNotNull("Nothing assigned to attempt " + request.getAttemptID(),
comment|//        assigned);
comment|//    Assert.assertEquals("assigned to wrong attempt", request.getAttemptID(),
comment|//        assigned.getTaskAttemptID());
comment|//    if (checkHostMatch) {
comment|//      Assert.assertTrue("Not assigned to requested host", Arrays.asList(
comment|//          request.getHosts()).contains(
comment|//          assigned.getContainer().getNodeId().toString()));
comment|//    }
comment|//
comment|//  }
comment|//
comment|//  //Mock RMContainerAllocator
comment|//  //Instead of talking to remote Scheduler,uses the local Scheduler
comment|//  public static class LocalRMContainerAllocator extends RMContainerAllocator {
comment|//    private static final List<TaskAttemptContainerAssignedEvent> events =
comment|//      new ArrayList<TaskAttemptContainerAssignedEvent>();
comment|//
comment|//    public static class AMRMProtocolImpl implements AMRMProtocol {
comment|//
comment|//      private ResourceScheduler resourceScheduler;
comment|//
comment|//      public AMRMProtocolImpl(ResourceScheduler resourceScheduler) {
comment|//        this.resourceScheduler = resourceScheduler;
comment|//      }
comment|//
comment|//      @Override
comment|//      public RegisterApplicationMasterResponse registerApplicationMaster(RegisterApplicationMasterRequest request) throws YarnRemoteException {
comment|//        RegisterApplicationMasterResponse response = recordFactory.newRecordInstance(RegisterApplicationMasterResponse.class);
comment|//        return response;
comment|//      }
comment|//
comment|//      public AllocateResponse allocate(AllocateRequest request) throws YarnRemoteException {
comment|//        List<ResourceRequest> ask = request.getAskList();
comment|//        List<Container> release = request.getReleaseList();
comment|//        try {
comment|//          AMResponse response = recordFactory.newRecordInstance(AMResponse.class);
comment|//          Allocation allocation = resourceScheduler.allocate(request.getApplicationAttemptId(), ask);
comment|//          response.addAllNewContainers(allocation.getContainers());
comment|//          response.setAvailableResources(allocation.getResourceLimit());
comment|//          AllocateResponse allocateResponse = recordFactory.newRecordInstance(AllocateResponse.class);
comment|//          allocateResponse.setAMResponse(response);
comment|//          return allocateResponse;
comment|//        } catch(IOException ie) {
comment|//          throw RPCUtil.getRemoteException(ie);
comment|//        }
comment|//      }
comment|//
comment|//      @Override
comment|//      public FinishApplicationMasterResponse finishApplicationMaster(FinishApplicationMasterRequest request) throws YarnRemoteException {
comment|//        FinishApplicationMasterResponse response = recordFactory.newRecordInstance(FinishApplicationMasterResponse.class);
comment|//        return response;
comment|//      }
comment|//
comment|//    }
comment|//
comment|//    private ResourceScheduler scheduler;
comment|//    LocalRMContainerAllocator(ResourceScheduler scheduler, Configuration conf) {
comment|//      super(null, new TestContext(events));
comment|//      this.scheduler = scheduler;
comment|//      super.init(conf);
comment|//      super.start();
comment|//    }
comment|//
comment|//    protected AMRMProtocol createSchedulerProxy() {
comment|//      return new AMRMProtocolImpl(scheduler);
comment|//    }
comment|//
comment|//    @Override
comment|//    protected void register() {}
comment|//    @Override
comment|//    protected void unregister() {}
comment|//
comment|//    @Override
comment|//    protected Resource getMinContainerCapability() {
comment|//      Resource res = recordFactory.newRecordInstance(Resource.class);
comment|//      res.setMemory(1024);
comment|//      return res;
comment|//    }
comment|//
comment|//    @Override
comment|//    protected Resource getMaxContainerCapability() {
comment|//      Resource res = recordFactory.newRecordInstance(Resource.class);
comment|//      res.setMemory(10240);
comment|//      return res;
comment|//    }
comment|//
comment|//    public void sendRequest(ContainerRequestEvent req) {
comment|//      sendRequests(Arrays.asList(new ContainerRequestEvent[]{req}));
comment|//    }
comment|//
comment|//    public void sendRequests(List<ContainerRequestEvent> reqs) {
comment|//      for (ContainerRequestEvent req : reqs) {
comment|//        handle(req);
comment|//      }
comment|//    }
comment|//
comment|//    //API to be used by tests
comment|//    public List<TaskAttemptContainerAssignedEvent> schedule() {
comment|//      //run the scheduler
comment|//      try {
comment|//        heartbeat();
comment|//      } catch (Exception e) {
comment|//        LOG.error("error in heartbeat ", e);
comment|//        throw new YarnException(e);
comment|//      }
comment|//
comment|//      List<TaskAttemptContainerAssignedEvent> result = new ArrayList(events);
comment|//      events.clear();
comment|//      return result;
comment|//    }
comment|//
comment|//    protected void startAllocatorThread() {
comment|//      //override to NOT start thread
comment|//    }
comment|//
comment|//    static class TestContext implements AppContext {
comment|//      private List<TaskAttemptContainerAssignedEvent> events;
comment|//      TestContext(List<TaskAttemptContainerAssignedEvent> events) {
comment|//        this.events = events;
comment|//      }
comment|//      @Override
comment|//      public Map<JobId, Job> getAllJobs() {
comment|//        return null;
comment|//      }
comment|//      @Override
comment|//      public ApplicationAttemptId getApplicationAttemptId() {
comment|//        return recordFactory.newRecordInstance(ApplicationAttemptId.class);
comment|//      }
comment|//      @Override
comment|//      public ApplicationId getApplicationID() {
comment|//        return recordFactory.newRecordInstance(ApplicationId.class);
comment|//      }
comment|//      @Override
comment|//      public EventHandler getEventHandler() {
comment|//        return new EventHandler() {
comment|//          @Override
comment|//          public void handle(Event event) {
comment|//            events.add((TaskAttemptContainerAssignedEvent) event);
comment|//          }
comment|//        };
comment|//      }
comment|//      @Override
comment|//      public Job getJob(JobId jobID) {
comment|//        return null;
comment|//      }
comment|//
comment|//      @Override
comment|//      public String getUser() {
comment|//        return null;
comment|//      }
comment|//
comment|//      @Override
comment|//      public Clock getClock() {
comment|//        return null;
comment|//      }
comment|//
comment|//      @Override
comment|//      public String getApplicationName() {
comment|//        return null;
comment|//      }
comment|//
comment|//      @Override
comment|//      public long getStartTime() {
comment|//        return 0;
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  public static void main(String[] args) throws Exception {
comment|//    TestRMContainerAllocator t = new TestRMContainerAllocator();
comment|//    t.testSimple();
comment|//    //t.testResource();
comment|//    t.testMapReduceScheduling();
comment|//  }
block|}
end_class

end_unit

