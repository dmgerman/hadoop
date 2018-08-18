begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager
package|package
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
name|applicationsmanager
package|;
end_package

begin_class
DECL|class|TestSchedulerNegotiator
specifier|public
class|class
name|TestSchedulerNegotiator
block|{
comment|//  private static RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
comment|//  private SchedulerNegotiator schedulerNegotiator;
comment|//  private DummyScheduler scheduler;
comment|//  private final int testNum = 99999;
comment|//
comment|//  private final RMContext context = new RMContextImpl(new MemStore());
comment|//  AppAttemptImpl masterInfo;
comment|//  private EventHandler handler;
comment|//  private Configuration conf = new Configuration();
comment|//  private class DummyScheduler implements ResourceScheduler {
comment|//    @Override
comment|//    public Allocation allocate(ApplicationId applicationId,
comment|//        List<ResourceRequest> ask, List<Container> release) throws IOException {
comment|//      ArrayList<Container> containers = new ArrayList<Container>();
comment|//      Container container = recordFactory.newRecordInstance(Container.class);
comment|//      container.setId(recordFactory.newRecordInstance(ContainerId.class));
comment|//      container.getId().setAppId(applicationId);
comment|//      container.getId().setId(testNum);
comment|//      containers.add(container);
comment|//      return new Allocation(containers, Resources.none());
comment|//    }
comment|//
comment|//
comment|//    @Override
comment|//    public void nodeUpdate(RMNode nodeInfo,
comment|//        Map<String, List<Container>> containers) {
comment|//    }
comment|//
comment|//    @Override
comment|//    public void removeNode(RMNode node) {
comment|//    }
comment|//
comment|//    @Override
comment|//    public void handle(ASMEvent<ApplicationTrackerEventType> event) {
comment|//    }
comment|//
comment|//    @Override
comment|//    public QueueInfo getQueueInfo(String queueName,
comment|//        boolean includeChildQueues,
comment|//        boolean recursive) throws IOException {
comment|//      return null;
comment|//    }
comment|//    @Override
comment|//    public List<QueueUserACLInfo> getQueueUserAclInfo() {
comment|//      return null;
comment|//    }
comment|//    @Override
comment|//    public void addApplicationIfAbsent(ApplicationId applicationId,
comment|//        ApplicationMaster master, String user, String queue, Priority priority,
comment|//        ApplicationStore store)
comment|//        throws IOException {
comment|//    }
comment|//
comment|//
comment|//    @Override
comment|//    public void addNode(RMNode nodeInfo) {
comment|//    }
comment|//
comment|//
comment|//    @Override
comment|//    public void recover(RMState state) throws Exception {
comment|//    }
comment|//
comment|//
comment|//    @Override
comment|//    public void reinitialize(Configuration conf,
comment|//        ContainerTokenSecretManager secretManager, RMContext rmContext)
comment|//        throws IOException {
comment|//    }
comment|//
comment|//
comment|//    @Override
comment|//    public Resource getMaximumResourceCapability() {
comment|//      // TODO Auto-generated method stub
comment|//      return null;
comment|//    }
comment|//
comment|//
comment|//    @Override
comment|//    public Resource getMinimumResourceCapability() {
comment|//      // TODO Auto-generated method stub
comment|//      return null;
comment|//    }
comment|//  }
comment|//
comment|//  @Before
comment|//  public void setUp() {
comment|//    scheduler = new DummyScheduler();
comment|//    schedulerNegotiator = new SchedulerNegotiator(context, scheduler);
comment|//    schedulerNegotiator.init(conf);
comment|//    schedulerNegotiator.start();
comment|//    handler = context.getDispatcher().getEventHandler();
comment|//    context.getDispatcher().init(conf);
comment|//    context.getDispatcher().start();
comment|//  }
comment|//
comment|//  @After
comment|//  public void tearDown() {
comment|//    schedulerNegotiator.stop();
comment|//  }
comment|//
comment|//  public void waitForState(ApplicationState state, AppAttemptImpl info) {
comment|//    int count = 0;
comment|//    while (info.getState() != state&& count< 100) {
comment|//      try {
comment|//        Thread.sleep(50);
comment|//      } catch (InterruptedException e) {
comment|//       e.printStackTrace();
comment|//      }
comment|//      count++;
comment|//    }
comment|//    Assert.assertEquals(state, info.getState());
comment|//  }
comment|//
comment|//  private class DummyEventHandler implements EventHandler<ASMEvent<AMLauncherEventType>> {
comment|//    @Override
comment|//    public void handle(ASMEvent<AMLauncherEventType> event) {
comment|//    }
comment|//  }
comment|//
comment|//  @Test
comment|//  public void testSchedulerNegotiator() throws Exception {
comment|//    ApplicationSubmissionContext submissionContext = recordFactory.newRecordInstance(ApplicationSubmissionContext.class);
comment|//    submissionContext.setApplicationId(recordFactory.newRecordInstance(ApplicationId.class));
comment|//    submissionContext.getApplicationId().setClusterTimestamp(System.currentTimeMillis());
comment|//    submissionContext.getApplicationId().setId(1);
comment|//
comment|//    masterInfo = new AppAttemptImpl(this.context, this.conf, "dummy",
comment|//        submissionContext, "dummyClientToken", StoreFactory
comment|//            .createVoidAppStore(), new AMLivelinessMonitor(context
comment|//            .getDispatcher().getEventHandler()));
comment|//    context.getDispatcher().register(ApplicationEventType.class, masterInfo);
comment|//    context.getDispatcher().register(ApplicationTrackerEventType.class, scheduler);
comment|//    context.getDispatcher().register(AMLauncherEventType.class,
comment|//        new DummyEventHandler());
comment|//    handler.handle(new ApplicationEvent(
comment|//        ApplicationEventType.ALLOCATE, submissionContext.getApplicationId()));
comment|//    waitForState(ApplicationState.LAUNCHING, masterInfo); // LAUNCHING because ALLOCATED automatically movesto LAUNCHING for now.
comment|//    Container container = masterInfo.getMasterContainer();
comment|//    Assert.assertTrue(container.getId().getId() == testNum);
comment|//  }
block|}
end_class

end_unit

